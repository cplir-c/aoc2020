
use std::borrow::ToOwned;


pub trait BFSProblem<'a> {
    type Candidate: ToOwned<Owned=Self::Candidate>;
    fn root_candidate(&'a self) -> Self::Candidate;
    fn is_impossible(&self, candidate: &Self::Candidate) -> bool;
    fn is_solution(&self, candidate: &Self::Candidate) -> bool;
    fn first_extension(&'a self, candidate: Self::Candidate) -> Option<Self::Candidate>;
    fn next_extension(&'a self, previous_child: Self::Candidate) -> Option<Self::Candidate>;

    /// used for mutable `Candidate`s
    fn remove_extension(&'a self, _candidate: Self::Candidate) {}
    fn recursive_backtrack(&'a self) -> Option<Self::Candidate> {
        self._recursive_backtrack(self.root_candidate())
    }
    fn _recursive_backtrack(&'a self, candidate: Self::Candidate) -> Option<Self::Candidate> {
        if self.is_impossible(&candidate) {
            return None;
        } else if self.is_solution(&candidate) {
            return Some(candidate);
        }
        {
            let mut possible_candidate = self.first_extension(candidate.to_owned());
            while let Some(new_candidate) = possible_candidate {
                let solution = self._recursive_backtrack(new_candidate.to_owned());
                if solution.is_some() {
                    return solution;
                }
                possible_candidate = self.next_extension(new_candidate);
            }
        }
        self.remove_extension(candidate);
        None
    }
    fn vec_backtrack(&'a self) -> Option<Self::Candidate> {
        self.vec_backtrack_with_capacity(16)
    }
    fn vec_backtrack_with_capacity(&'a self, capacity: usize) -> Option<Self::Candidate> {
        let root_candidate = self.root_candidate();
        println!("rooted search");
        let mut candidate_vec = Vec::with_capacity(capacity);
        candidate_vec.push(root_candidate);
        println!("pushed search root");
        loop {
            // descended to this one
            let candidate = match candidate_vec.last() {
                // if the root was popped, we failed to find a solution
                None => { return None; },
                Some(candidate) => candidate.to_owned()
            };
            println!("copied top candidate");
            
            if self.is_solution(&candidate) {
                println!("candidate is the solution!");
                return Some(candidate);
            } else if self.is_impossible(&candidate) {
                println!("candidate is impossible, backtracking");
                go_backtrack(self, &mut candidate_vec, candidate.to_owned());
            }
            
            if let Some(new_candidate) = self.first_extension(candidate.to_owned()) {
                println!("added new candidate, new depth: {}", candidate_vec.len());
                candidate_vec.push(new_candidate);
            } else {
                println!("failed to extend candidate, backtracking");
                go_backtrack(self, &mut candidate_vec, candidate.to_owned());
            }
        }
    }
}

fn go_backtrack<'a, 'b, S: ?Sized, C: ToOwned<Owned=C>>
  ( this: &'a S
  , candidate_vec: &'b mut Vec<C>
  , candidate: C
  ) where S: BFSProblem<'a, Candidate=C> {
    // if this candidate is impossible, try the next one
    let possible_candidate = this.next_extension(candidate.to_owned());
    if let Some(candidate) = possible_candidate {
        print!("found sibiling candidate to impossible candidate, ");
        if let Some(vec_spot) = candidate_vec.last_mut() {
            println!("replaced candidate");
            *vec_spot = candidate;
        } else {
            panic!("Shouldn't have been empty if there was an impossible candidate to get here")
        }
    } else {
        println!("impossible candidate has no sibilings remaining");
        while let Some(candidate) = candidate_vec.last_mut() {
            match this.next_extension(candidate.to_owned()) {
                None => {
                    // should be impossible for the vec pop
                    // to not exist if last_mut succeeded
                    this.remove_extension(candidate_vec.pop().unwrap());
                    println!("popped candidate, new depth: {}", candidate_vec.len() - 1)
                },
                Some(next_candidate) => {
                    println!("this candidate has a sibling, replacing candidate");
                    *candidate = next_candidate;
                    break
                }
            };
        }
    }
}
