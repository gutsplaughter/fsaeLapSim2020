Imports System.IO
Imports System.Text.RegularExpressions

Public Class Form1
    Private Sub Button3_Click(sender As Object, e As EventArgs) Handles Button3.Click
        End
    End Sub

    Private Sub Button1_Click(sender As Object, e As EventArgs) Handles Button1.Click
        For Each p As Process In Process.GetProcesses
            If p.ProcessName = "cmd" Then
                Try
                    p.Kill()
                Catch ex As Exception
                    Continue For
                End Try
            End If
        Next
        Dim OpenCMD
        OpenCMD = CreateObject("wscript.shell")
        OpenCMD.run(".\runLapSimDefaultParams.bat")
    End Sub

    Private Sub Button5_Click(sender As Object, e As EventArgs) Handles Button5.Click
        LoadParameters("..\..\..\car.txt")
    End Sub

    Function ReadLineWithNumberFrom(filePath As String, ByVal lineNumber As Integer) As String
        Using file As New StreamReader(filePath)
            ' Skip all preceding lines: '
            For i As Integer = 1 To lineNumber - 1
                If file.ReadLine() Is Nothing Then
                    Throw New ArgumentOutOfRangeException("lineNumber")
                End If
            Next
            ' Attempt to read the line you're interested in: '
            Dim line As String = file.ReadLine()
            If line Is Nothing Then
                Throw New ArgumentOutOfRangeException("lineNumber")
            End If
            ' Succeded!
            Return line
        End Using
    End Function

    Function LoadParameters(file As String)
        Dim line As String = (ReadLineWithNumberFrom(file, 1))
        Dim mass As Double = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox1.Text = mass
        line = (ReadLineWithNumberFrom(file, 2))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox2.Text = mass
        line = (ReadLineWithNumberFrom(file, 3))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox3.Text = mass
        line = (ReadLineWithNumberFrom(file, 4))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox4.Text = mass
        line = (ReadLineWithNumberFrom(file, 5))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox5.Text = mass
        line = (ReadLineWithNumberFrom(file, 6))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox6.Text = mass
        line = (ReadLineWithNumberFrom(file, 7))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.+\-]", ""))
        TextBox7.Text = mass
        line = (ReadLineWithNumberFrom(file, 8))
        mass = Double.Parse(Regex.Replace(line.Substring(0, line.Length - 1), "[^\d+\.]", ""))
        TextBox8.Text = mass
        line = (ReadLineWithNumberFrom(file, 9))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox11.Text = mass
        line = (ReadLineWithNumberFrom(file, 10))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox12.Text = mass
        line = (ReadLineWithNumberFrom(file, 11))
        mass = Double.Parse(Regex.Replace(line.Substring(1), "[^\d+\.]", ""))
        TextBox13.Text = mass
        line = (ReadLineWithNumberFrom(file, 12))
        mass = Double.Parse(Regex.Replace(line.Substring(1), "[^\d+\.]", ""))
        TextBox14.Text = mass
        line = (ReadLineWithNumberFrom(file, 13))
        mass = Double.Parse(Regex.Replace(line.Substring(1), "[^\d+\.]", ""))
        TextBox15.Text = mass
        line = (ReadLineWithNumberFrom(file, 14))
        mass = Double.Parse(Regex.Replace(line.Substring(1), "[^\d+\.]", ""))
        TextBox16.Text = mass
        line = (ReadLineWithNumberFrom(file, 15))
        mass = Double.Parse(Regex.Replace(line.Substring(1), "[^\d+\.]", ""))
        TextBox19.Text = mass
        line = (ReadLineWithNumberFrom(file, 16))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox20.Text = mass
        line = (ReadLineWithNumberFrom(file, 17))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox21.Text = mass
        line = (ReadLineWithNumberFrom(file, 18))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox22.Text = mass
        line = (ReadLineWithNumberFrom(file, 19))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox23.Text = mass
        line = (ReadLineWithNumberFrom(file, 20))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox24.Text = mass
        line = (ReadLineWithNumberFrom(file, 21))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox25.Text = mass
        line = (ReadLineWithNumberFrom(file, 22))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox10.Text = mass
        line = (ReadLineWithNumberFrom(file, 23))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox9.Text = mass
        line = (ReadLineWithNumberFrom(file, 24))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox26.Text = mass
        line = (ReadLineWithNumberFrom(file, 25))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox18.Text = mass
        line = (ReadLineWithNumberFrom(file, 26))
        mass = Double.Parse(Regex.Replace(line, "[^\d+\.]", ""))
        TextBox17.Text = mass
    End Function

    Function SaveParameters()
        System.IO.File.WriteAllText("..\..\..\car.txt", "")
        Dim file As System.IO.StreamWriter
        file = My.Computer.FileSystem.OpenTextFileWriter("..\..\..\car.txt", True)
        file.WriteLine("mass " & TextBox1.Text & " kg")
        file.WriteLine("driverMass " & TextBox2.Text & " kg")
        file.WriteLine("power " & TextBox3.Text & " hp")
        file.WriteLine("brakingTorqueFront " & TextBox4.Text & " N*m")
        file.WriteLine("brakingTorqueRear " & TextBox5.Text & " N*m")
        file.WriteLine("cD " & TextBox6.Text)
        file.WriteLine("cL " & TextBox7.Text)
        file.WriteLine("frontalArea " & TextBox8.Text & " m^2")
        file.WriteLine("primaryRatio " & TextBox11.Text)
        file.WriteLine("finalDrive " & TextBox12.Text)
        file.WriteLine("1stGear " & TextBox13.Text)
        file.WriteLine("2ndGear " & TextBox14.Text)
        file.WriteLine("3rdGear " & TextBox15.Text)
        file.WriteLine("4thGear " & TextBox16.Text)
        file.WriteLine("5thGear " & TextBox19.Text)
        file.WriteLine("shiftTime " & TextBox20.Text & " s")
        file.WriteLine("tireRadius " & TextBox21.Text & " ft")
        file.WriteLine("wheelbase " & TextBox22.Text & " m")
        file.WriteLine("trackFront " & TextBox23.Text & " m")
        file.WriteLine("trackRear " & TextBox24.Text & " m")
        file.WriteLine("CGheight " & TextBox25.Text & " m")
        file.WriteLine("CGfront " & TextBox10.Text & " %")
        file.WriteLine("rearRollStiffness " & TextBox9.Text & " N*m/rad")
        file.WriteLine("frontRollStiffness " & TextBox26.Text & " N*m/rad")
        file.WriteLine("rearRollCenterHeight " & TextBox18.Text & " m")
        file.Write("frontRollCenterHeight " & TextBox17.Text & " m")
        file.Close()
    End Function

    Function FindLastModified() As String
        Dim strFilepath = ""  'Specify path details
        Dim directory As New System.IO.DirectoryInfo(strFilepath)
        Dim File As System.IO.FileInfo() = directory.GetFiles()
        Dim File1 As System.IO.FileInfo
        For Each File1 In File
            Dim strLastModified As String
            strLastModified = System.IO.File.GetLastWriteTime(strFilepath & "\" & File1.Name).ToShortDateString()
        Next
    End Function

    Private Sub Form1_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        Me.BackColor = Color.FromArgb(249, 235, 222)
        LoadParameters("..\..\..\car.txt")
    End Sub

    Private Sub Button6_Click(sender As Object, e As EventArgs) Handles Button6.Click
        SaveParameters()
    End Sub

    Private Sub Button4_Click(sender As Object, e As EventArgs) Handles Button4.Click
        For Each p As Process In Process.GetProcesses
            If p.ProcessName = "cmd" Then
                Try
                    p.Kill()
                Catch ex As Exception
                    Continue For
                End Try
            End If
        Next
        Dim OpenCMD
        OpenCMD = CreateObject("wscript.shell")
        OpenCMD.run(".\runLapSim.bat")
    End Sub

    Private Sub Button2_Click(sender As Object, e As EventArgs) Handles Button2.Click
        For Each p As Process In Process.GetProcesses
            If p.ProcessName = "cmd" Then
                Try
                    p.Kill()
                Catch ex As Exception
                    Continue For
                End Try
            End If
        Next
        Dim OpenCMD
        OpenCMD = CreateObject("wscript.shell")
        OpenCMD.run(".\runSensitivityStudy.bat")
    End Sub

    Private Sub Button7_Click(sender As Object, e As EventArgs) Handles Button7.Click
        LoadParameters("..\..\..\x49car.txt")
    End Sub

    Private Sub Button8_Click(sender As Object, e As EventArgs) Handles Button8.Click
        Dim strFilepath = "..\..\..\output logs\"  'Specify path details
        Dim directory As New System.IO.DirectoryInfo(strFilepath)
        Dim File As System.IO.FileInfo() = directory.GetFiles()
        Dim File1 As System.IO.FileInfo
        Dim newestFile As System.IO.FileInfo = File(0)
        For Each File1 In File
            If (File1.LastAccessTime > newestFile.LastAccessTime) Then
                newestFile = File1
            End If
        Next
        System.Diagnostics.Process.Start(newestFile.FullName)
    End Sub
End Class
