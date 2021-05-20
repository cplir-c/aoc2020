use std::cmp::Ordering;
use std::fmt;
use std::fmt::Write;
use std::iter::repeat;
use std::ops::Deref;
use unicode_segmentation::UnicodeSegmentation;

pub fn invert_uint_function<F: Fn(usize) -> usize>(target: usize, f: F) -> usize {
    let mut upper = target >> 1;
    let mut lower = 0;
    while upper - lower > 1 {
        let middle_bound = (upper + lower) >> 1;
        let middle_attempt = f(middle_bound);
        match std::cmp::Ord::cmp(&middle_attempt, &target) {
            Ordering::Less => { lower = middle_bound; },
            Ordering::Equal => { return middle_bound; },
            Ordering::Greater => { upper = middle_bound; }
        };
    }
    (upper + lower) >> 1
}

pub fn isqrt(area: usize) -> usize {
    invert_uint_function(area, |x|{ x * x })
}

pub fn reverse_str<S: From<String>>(forward_str: &str) -> S {
    let mut string = forward_str.to_string();
    unicode_reverse::reverse_grapheme_clusters_in_place(&mut string);
    S::from(string)
}

pub fn write_line(out: &mut fmt::Formatter, line: &str) -> fmt::Result {
    write!(out, "{}", line)
}

pub fn write_line_backwards(out: &mut fmt::Formatter, line: &str) -> fmt::Result {
    UnicodeSegmentation::graphemes(line, true).try_rfold((), |_unit, grap| {
        write!(out, "{}", grap)
    })
}
pub fn write_right_char(out: &mut fmt::Formatter, line: &mut &str) -> fmt::Result {
    let last: &str = UnicodeSegmentation::graphemes(*line, true).next_back().expect("shouldn't break cause of the range limits");
    let graplen = last.len();
    *line = &line[..line.len()-graplen];
    write!(out, "{}", last)
}
pub fn write_left_char(out: &mut fmt::Formatter, line: &mut &str) -> fmt::Result {
    let first: char = line.chars().next().expect("shouldn't break cause of the range limits");
    let chrlen = first.len_utf8();
    *line = &line[chrlen..];
    write!(out, "{}", first)
}

pub struct SquareFormat<'a, 'b>(pub &'b mut fmt::Formatter<'a>);

impl<'a, 'b> SquareFormat<'a, 'b>{
    pub fn write_lines_down<F: FnMut(&mut fmt::Formatter, &str) -> fmt::Result>(self, lines: &str, mut write_line: F) -> fmt::Result {
        lines.lines().try_fold((), |_unit, line: &str|{
            write_line(self.0, line)
        })
    }
    pub fn write_lines_up<F: FnMut(&mut fmt::Formatter, &str) -> fmt::Result>(self, lines: &str, mut write_line: F) -> fmt::Result {
        lines.lines().try_rfold((), |_unit, line: &str|{
            write_line(self.0, line)
        })
    }
    pub fn write_columns_down<F: FnMut(&mut fmt::Formatter, &mut &str) -> fmt::Result>(self, lines: &str, mut write_char: F) -> fmt::Result {
        let mut lines: Vec<&str> = lines.lines().collect();
        let edge_length = lines.get(0).map(|line|{ line.len() }).unwrap_or(10);
        (0..edge_length).try_fold((), |_unit, _col_index|{
            lines.as_mut_slice().iter_mut().try_fold((), |_unit, line: &mut &str| {
                write_char(self.0, line)
            })
        })
    }
    pub fn write_columns_up<F: FnMut(&mut fmt::Formatter, &mut &str) -> fmt::Result>(self, lines: &str, mut write_char: F) -> fmt::Result {
        let mut lines: Vec<&str> = lines.lines().collect();
        let edge_length = lines.get(0).map(|line|{ line.len() }).unwrap_or(10);
        (0..edge_length).try_fold((), |_unit, _col_index|{
            lines.as_mut_slice().iter_mut().try_rfold((), |_unit, line: &mut &str| {
                write_char(self.0, line)
            })
        })
    }
}

pub struct MapDisplay<'a, T: fmt::Display>(pub &'a [T]);

impl<'a, 'b, T: fmt::Display> fmt::Display for MapDisplay<'a, T> {
    fn fmt(&self, formatter: &mut fmt::Formatter) -> fmt::Result {
        let tile_count = self.0.len();
        writeln!(formatter, "count: {}", tile_count)?;
        if tile_count == 0 {
            return Ok(());
        }
        let edge_length = isqrt(tile_count);
        
        let mut sizes_vec = vec![0; edge_length * 2];
        let (heights, widths): (&mut [usize], &mut [usize]) = sizes_vec.as_mut_slice().split_at_mut(edge_length);
        let mut tile_strings: Vec<Box<str>> = Vec::with_capacity(tile_count);
        
        let mut size_guess: Option<usize> = None;
        for (row, chunk) in self.0.chunks(edge_length).enumerate() {
            for (col, tile) in chunk.iter().enumerate() {
                let mut string_buf: String = match size_guess{
                    Some(len) => String::with_capacity(len),
                    None => String::new()
                };
                write!(string_buf, "{}", tile)?;
                if size_guess.unwrap_or_else(||string_buf.len()) >= string_buf.len() {
                    size_guess = Some(string_buf.len());
                }
                
                let height = string_buf.lines().count();
                let width = string_buf.lines().map(&str::len).max().unwrap_or(0);
                if heights[row] < height {
                    heights[row] = height;
                }
                if widths[col] < width {
                    widths[col] = width;
                }
                
                tile_strings.push(string_buf.into_boxed_str());
            }
        }
        let total_width: usize = widths.iter().sum::<usize>() + edge_length;
        let total_height: usize = heights.iter().sum::<usize>() + edge_length - 1;
        let max_height: usize = *heights.iter().max().unwrap_or(&1);
        
        let spaces = |n| {repeat(' ').take(n)};
        let mut tile_strs: Vec<&str> = Vec::with_capacity(edge_length);
        let mut buf: String = String::with_capacity(total_width);
        for (row, chunk) in tile_strings.chunks(edge_length).enumerate() {
            let row_height = heights[row];
            tile_strs.clear();
            tile_strs.extend(chunk.iter().map(Deref::deref));
            for _i in 0..row_height + 1 {
                for (col, tile_str) in tile_strs.iter_mut().enumerate() {
                    let col_width = widths[col];
                    let (line, rest) = tile_str.split_once('\n')
                        .unwrap_or(("", ""));
                    let line_len = line.len();
                    *tile_str = rest;
                    buf.push_str(line);
                    buf.extend(spaces(col_width - line_len + 1));
                }
                if buf.pop() != Some(' ') {
                    return Err(fmt::Error);
                }
                writeln!(formatter, "{}", buf)?;
                buf.clear();
            }
        }
        Ok(())
    }
}




