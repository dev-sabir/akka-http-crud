package learning


import akka.http.scaladsl.Http

import akka.actor.ActorSystem

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import learning.json.JsonUtility._


object SimpleCRUD {

  implicit val system: ActorSystem = ActorSystem("RestServiceApp")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  var studentList: scala.collection.immutable.Seq[String] = Nil




  def main(args: Array[String]): Unit = {
    val route: Route = concat(
      //Adding Students
      post {
        path("addstudent") {
          entity(as[String]) {
            studentJson =>
              complete {
                val student = parse(studentJson).extract[Student]
                if(StudentDB.getByRollNo(student.rollNo).isEmpty) {
                  StudentDB.save(student)
                  "Student Saved Successfully"
                }else{
                  "Roll No. already exists!!"
                }
              }

          }
        }

      },
      //Get List of All Student
      get{
        path("allstudents"){
          complete{
            val students: List[Student] = StudentDB.getAll()
            students match {
              case Nil => "No Students in the List"
              case _ => write(students)
            }
          }
        }
      },
//       Get Student by Roll No
      get{
        path("student"){
          parameters('rollno.as[Int]){rollno =>
            complete{
              val student: List[Student] = StudentDB.getByRollNo(rollno)
              if(student.isEmpty){
                s"Student with roll no: $rollno does not exist"
              }else {
                write(student)
              }
            }

          }
        }
      },
      // Delete with roll. No
      delete{
        path("removestudent"){
          parameters('rollno.as[Int]){ rollno =>
          complete{
            val isStudent: List[Student] = StudentDB.getByRollNo(rollno)
            if(isStudent.isEmpty){
              s"Student with roll no $rollno doesn't exists"
            }else{
              val updatedStudentList = StudentDB.deleteStudentByRollNo(rollno)
              s"Updated Student List: ${write(updatedStudentList)}"

            }

          }

          }
        }

      },
      // Updating Student
      put{
        path("updatestudent"){
          entity(as[String]){
            studentJson => complete{
              val student = parse(studentJson).extract[Student]
              val isStudent = StudentDB.getByRollNo(student.rollNo)
              if(isStudent.isEmpty){
                s"Student doesn't exists"
              }else{
                val (oldStudent, updatedStudent) = StudentDB.updateStudent(student)
                s"Old Student: $oldStudent \nUpdated Student: $updatedStudent"
              }
            }
          }
        }
      }
    )



    val port = 8000
    Http().newServerAt("localhost", port).bindFlow(route)

    println(s"Server is running on port $port")
  }
}
