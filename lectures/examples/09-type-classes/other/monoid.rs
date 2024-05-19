trait Monoid {
    fn identity() -> Self;
    fn combine(&self, subject: Self) -> Self;
}

impl Monoid for i32 {
    fn identity() -> i32 { 0 }
    fn combine(&self, b: i32) -> i32 { *self + b }
}

fn main() {
   println!("{}", i32::identity());
   println!("{}", 1.combine(2).to_string());
}
