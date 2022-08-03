package learning

object StudentDB {


  private var students: List[Student] = Nil

  def getAll(): List[Student] = students

  def getByRollNo(rollNo: Int): List[Student] = students.filter(_.rollNo == rollNo)

  def save(student: Student): Unit = {
    students = students :+ student
  }

  def deleteStudentByRollNo(rollno: Int): List[Student] = {
    students = students.filter(_.rollNo != rollno)
    students
  }

  def updateStudent(student: Student):(Student, Student) ={
    val oldStudent: Student = students.filter(_.rollNo == student.rollNo)(0)


    students = students.patch(students.indexOf(oldStudent), Seq(student),1)
    (oldStudent, student)
  }

}



case class Student(rollNo: Int, fName: String, lName: String, branch: String, gYear: Int)
