
use super::Side;
use super::SideIterator;

#[derive(Debug, Default, Copy, Clone)]
pub struct TileOrientation {
    pub top_side: Side,
    pub top_flipped: bool
}

#[derive(Debug, Copy, Clone)]
pub struct TileOrientationIterator (Option<TileOrientation>);

impl Default for TileOrientationIterator {
    fn default() -> Self {
        TileOrientationIterator(Some(TileOrientation::default()))
    }
}

impl IntoIterator for TileOrientation {
    type Item = TileOrientation;
    type IntoIter = TileOrientationIterator;
    fn into_iter(self) -> TileOrientationIterator {
        TileOrientationIterator (Some(self))
    }
}

impl Iterator for TileOrientationIterator {
    type Item = TileOrientation;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        let current = self.0;
        self.0 = self.0.and_then(|orientation| match orientation {
            // flip side if not flipped
            TileOrientation{top_side: side, top_flipped: false}
                => Some(TileOrientation{top_side: side, top_flipped: true}),
            // Turn the tile more if possible, otherwise put None
            TileOrientation{top_side: side, top_flipped: true}
                => SideIterator(Some(side)).next_side().map(|option_side|{
                    TileOrientation {top_side: option_side, top_flipped: false}
                })
        });
        current
    }
}