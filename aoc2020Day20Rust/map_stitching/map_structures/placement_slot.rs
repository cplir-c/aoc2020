
use std::borrow::Borrow;

#[repr(transparent)]
pub struct PlacementSlot<'a, T>(Option<&'a T>);
impl<'a, T> From<Option<&'a T>> for PlacementSlot<'a, T> {
    fn from(option_tile_placement: Option<&'a T>) -> Self {
        PlacementSlot(option_tile_placement)
    }
}
impl<'a, T> From<&'a T> for PlacementSlot<'a, T> {
    fn from(tile_ref: &'a T) -> Self {
        PlacementSlot::from(Some(tile_ref))
    }
}
impl<'a, T> From<&'a MutPlacementSlot<'a, T>> for PlacementSlot<'a, T> {
    fn from(mut_place: &'a MutPlacementSlot<'a, T>) -> Self {
        let inner: Option<&'a T> = mut_place.0.as_ref().map(|mut_ref| {
            mut_ref.borrow()
        });
        PlacementSlot(inner)
    }
}
impl<'a, T> From<&'a PlacementSlot<'a, T>> for &'a Option<&'a T> {
    fn from(placement_slot: &'a PlacementSlot<'a, T>) -> Self {
        &placement_slot.0
    }
}
impl<'a, T> From<PlacementSlot<'a, T>> for Option<&'a T> {
    fn from(placement_slot: PlacementSlot<'a, T>) -> Self {
        placement_slot.0
    }
}

#[repr(transparent)]
pub struct MutPlacementSlot<'a, T>(Option<&'a mut T>);
impl<'a, T> From<Option<&'a mut T>> for MutPlacementSlot<'a, T> {
    fn from(option_tile_placement: Option<&'a mut T>) -> Self {
        MutPlacementSlot(option_tile_placement)
    }
}
impl<'a, T> From<&'a mut T> for MutPlacementSlot<'a, T> {
    fn from(tile_ref: &'a mut T) -> Self {
        MutPlacementSlot::from(Some(tile_ref))
    }
}
impl<'a, T> MutPlacementSlot<'a, T> {
    /// The caller certifies that there are no mut reference holders to this placement slot
    unsafe fn as_ref(&self) -> &PlacementSlot<'a, T> {
       // the only difference between the structures is this one has a mut reference instead of a shared reference
       std::mem::transmute::<&MutPlacementSlot<'a, T>, &PlacementSlot<'a, T>>(&self)
    }
}
impl<'a, T> From<&'a MutPlacementSlot<'a, T>> for &'a Option<&'a mut T> {
    fn from(mut_placement_slot: &'a MutPlacementSlot<'a, T>) -> Self {
        &mut_placement_slot.0
    }
}
impl<'a, T> From<MutPlacementSlot<'a, T>> for Option<&'a mut T> {
    fn from(mut_placement_slot: MutPlacementSlot<'a, T>) -> Self {
        mut_placement_slot.0
    }
}
