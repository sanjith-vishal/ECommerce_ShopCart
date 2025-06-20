import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonService } from '../common.service';
import { RegisterService } from '../register.service';
 
@Component({
  selector: 'registration',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {
  myForm:FormGroup;
  userRole:boolean;
  constructor(private router:Router,private fb:FormBuilder,private commonService:CommonService,private regService:RegisterService){

  }
  ngOnInit(): void {
    this.myForm = this.fb.group({
      name:['',[Validators.required,Validators.minLength(4),Validators.maxLength(15)]],
      email:['',[Validators.required,Validators.email]],
      password:['',[Validators.required]],
      confirmPassword:['',[Validators.required]],
      roles:['USER',[Validators.required]],
      phoneNumber:[''],
      shippingAddress:[''],
    },{validator:this.passwordMatchValidator})
  }

  passwordMatchValidator(form : FormGroup){
    const password = form.get("password").value;
    const conPassword = form.get("confirmPassword").value;
    return password === conPassword ? null :{mismatch:true}
  }
  onReset() :void{
    this.myForm.reset();
  }
  submit(form) : void{
    if(this.myForm.valid){
      const role = this.myForm.get('roles') ?.value;
      console.log(role)
      if(role == "USER"){
          this.submitUser(form);
      }
      else{
        this.submitAdmin(form);
      }
    }
  }
  submitUser(form):void{
    console.log("inside submit user", form.value);
   
    this.regService.registerBoth1(form.value).subscribe(response => {console.log(response)})
    
    const role=form.value.roles 
    console.log(role)
    alert("Successfully Registered. Redirecting you to Login Page!!");
    this.router.navigate(["/login"]);
  }
  submitAdmin(form):void{
    console.log("inside submit Admin", form.value);
   
    this.regService.registerBoth2(form.value).subscribe(response => {console.log(response)})
    
    const role=form.value.roles
    console.log(role)
    alert("Successfully Registered. Redirecting you to Login Page!!");
    this.router.navigate(["/login"]);
  }
}