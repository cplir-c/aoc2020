use std::borrow::Borrow;
use std::fmt;
use std::fmt::Debug;
use std::fmt::Display;
use std::fmt::Formatter;

#[repr(transparent)]
pub struct DebugStrWrapper<S: Borrow<str>>{
    s: S
}
impl<S: Borrow<str>> Display for DebugStrWrapper<S> {
    fn fmt(&self, out: &mut Formatter) -> fmt::Result {
        out.write_str(self.borrow())
    }
}
impl<S: Borrow<str>> Debug for DebugStrWrapper<S> {
    fn fmt(&self, out: &mut Formatter) -> fmt::Result {
        out.write_str(self.borrow())
    }
}
impl<S: Borrow<str>> Borrow<str> for DebugStrWrapper<S> {
    fn borrow(&self) -> &str {
        self.s.borrow()
    }
}

#[repr(transparent)]
pub struct DisplayAdapter<D: Display> {
    display: D
}
impl<D: Display> Debug for DisplayAdapter<D> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        Display::fmt(&self.display, fmt)
    }
}
impl<D: Display> Display for DisplayAdapter<D> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        Display::fmt(&self.display, fmt)
    }
}

#[repr(transparent)]
pub struct DebugAdapter<D: Debug> {
    debug: D
}
impl<D: Debug> Debug for DebugAdapter<D> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        Debug::fmt(&self.debug, fmt)
    }
}
impl<D: Debug> Display for DebugAdapter<D> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        Debug::fmt(&self.debug, fmt)
    }
}

#[repr(transparent)]
pub struct ListWrapper<'a, I> {
    iterable: &'a I
}
impl<'a, I> ListWrapper<'a, I> {
    pub fn new(iterable: &'a I) -> ListWrapper<'a, I> {
        ListWrapper{ iterable }
    }
}
impl<'a, I> Display for ListWrapper<'a, I>
  where &'a I: IntoIterator,
  <&'a I as IntoIterator>::Item: Display {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        fmt.debug_list().entries(<&I>::into_iter(&self.iterable).map(|itm| DisplayAdapter { display: itm })).finish()
    }
}

#[repr(transparent)]
pub struct SetWrapper<'a, I> {
    iterable: &'a I
}
impl<'a, I> SetWrapper<'a, I> {
    pub fn new(iterable: &'a I) -> SetWrapper<'a, I> {
        SetWrapper{ iterable }
    }
}
impl<'a, I> Display for SetWrapper<'a, I>
  where &'a I: IntoIterator,
  <&'a I as IntoIterator>::Item: Display {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        fmt.debug_set().entries(<&I>::into_iter(&self.iterable).map(|itm| DisplayAdapter { display: itm })).finish()
    }
}

#[repr(transparent)]
pub struct OptionPrintWrapper<'a, T:?Sized>(Option<&'a T>);
impl<'a, T:?Sized + Display> Display for OptionPrintWrapper<'a, T> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        match self.0 {
            None => write!(fmt, " "),
            Some(t) => write!(fmt, "{}", t)
        }
    }
}
impl<'a, T:?Sized + Display> Debug for OptionPrintWrapper<'a, T> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        write!(fmt, "{:?}", self.0.map(|display| DisplayAdapter{ display }))
    }
}
