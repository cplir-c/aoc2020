
use super::isqrt;

#[test]
fn test_isqrt_data() {//   0  1  2  3  4  5  6  7  8  9
	let good_sqrts = [0, 1, 1, 1, 2, 2, 2, 2, 2, 3];
	for (i, sqrt) in good_sqrts.iter().enumerate() {
		let found = isqrt(i);
		assert!(found == *sqrt, "failed isqrt data check sqrt({}) == {} != {}", i, sqrt, found);
	}
}

#[test]
fn test_isqrt_auto() {
	for i in 0..=121 {
		let i: u32 = i;
		let found = isqrt(i);
		assert!(found * found <= i, "failed auto isqrt less than check");
	}
	for i in 0..=144 {
		let j: u32 = i * i;
		let found = isqrt(j);
		assert!(found == i, "failed auto isqrt squares check");
	}
}