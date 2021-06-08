
use std::borrow::Borrow;
use std::borrow::ToOwned;
use std::convert::AsRef;

pub enum BorrowOwning<'a, T: Borrow<B>, B: ?Sized> {
    Borrowed(&'a B),
    Owned(T)
}
impl<'a, T: Borrow<B>, B: ?Sized> ToOwned for BorrowOwning<'a, T, B> {
    type Owned = BorrowOwning<'a, T, B>;
    fn to_owned(&self) -> Self {
        todo!()
        //Self::Borrowed(BorrowOwning::borrow(&self))
    }
}
impl<'a, T: Borrow<B>, B: ?Sized> Borrow<B> for BorrowOwning<'a, T, B> {
    fn borrow(&self) -> &B {
        match self {
            Self::Borrowed(bor) => bor,
            Self::Owned(o) => o.borrow(),
        }
    }
}
impl<'a, T: Borrow<B>, B: ?Sized> AsRef<B> for BorrowOwning<'a, T, B> {
    fn as_ref(&self) -> &B {
        match self {
            Self::Borrowed(bor) => bor,
            Self::Owned(o) => o.borrow(),
        }
    }
}

pub enum RefOwning<'a, T: 'a + AsRef<B>, B: 'a + ?Sized> {
    Referenced(&'a B),
    Owned(T)
}
impl<'a, T: AsRef<B>, B: 'a + ?Sized> ToOwned for RefOwning<'a, T, B> {
    type Owned = RefOwning<'a, T, B>;
    fn to_owned(&self) -> Self {
        //Self::Referenced(self.as_ref())
        todo!()
    }
}
impl<'a, T: 'a + AsRef<B>, B: 'a + ?Sized> AsRef<B> for RefOwning<'a, T, B> {
    fn as_ref(&self) -> &B {
        match self {
            Self::Referenced(bor) => bor,
            Self::Owned(o) => o.as_ref(),
        }
    }
}
impl<'a, T: AsRef<B>, B: ?Sized> Borrow<B> for RefOwning<'a, T, B> {
    fn borrow(&self) -> &B {
        match self {
            Self::Referenced(bor) => bor,
            Self::Owned(o) => o.as_ref(),
        }
    }
}