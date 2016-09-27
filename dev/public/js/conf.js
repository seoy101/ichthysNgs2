/**
 * 
 */
var dialogInstance1 =null;

var jobNameValidation = function(){
  var validation;
  var cnt =0;
  
  $.ajax({
    url:'http://localhost:9000/views/jobList',
    type:'GET',
    success: function(data) {
      $("#list").html(data);    
    }
 });
  $('#parentInfodiv').hide();
  $('#jobName').focusout(function() {
    validation = 0;  
    var jobname = $('#jobName').val();    
    $.ajax({
      type: 'POST' ,
      url: "http://localhost:9000/jobnamedupcheck" ,
      data: {"jobName" : jobname} ,
      success: function(e) {          
        if(e === "0") {
        } else {
          alert("duplicated job name");
          validation = 1;            
        }
      }               
    });    
  });
  $('#jobType').change(function(){
    var jobproperty = $('#jobType').val();
    if(jobproperty=='Child'){
      $('#parentInfodiv').show();
    }
    else{$('#parentInfodiv').hide();}
  });
 $('#create-button').click(function(){
   conf.jobName=$("#jobName").val();
   conf.jobType=$("#jobType").val();
   conf.parentInfo=$("#parentInfo").val();
   conf.cpu=$("#cpu").val();
   conf.mem=$("#mem").val();
   
    dialogInstance1 = new BootstrapDialog({
    modal : false,
    draggable : true,
    animate : false,
    message : $('<div></div>').load('views/pipeline'),
    });
    
    dialogInstance1.open();
  });
}

//$(document).ready(jobNameValidation);