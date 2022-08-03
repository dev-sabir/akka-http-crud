//case class Student(name: String, rollno: Int)
//val l: List[Studnets]
val x = List(10,20,30,40,50,60)
x.patch(x.indexOf(30), Seq(3),1)