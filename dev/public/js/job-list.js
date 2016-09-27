/**
 * 
 */
$(function() {  
  var arr = [];
  $.ajax({
    type: 'GET' ,
    url: "http://localhost:9000/getImageTable" ,
    success: function(e) {
      var a1 = JSON.parse(e);
      var x;
      for( x in a1) {
        var imageobject = new Object();
        imageobject.pIndex = a1[x].pIndex;
        imageobject.jobName = a1[x].jobName;
        imageobject.jobType = a1[x].jobType;
        imageobject.parentInfo = a1[x].parentInfo;
        imageobject.status = a1[x].status;
        imageobject.date = a1[x].date;
        imageobject.uId = a1[x].uId;
        arr.push(imageobject);
      }
     
      var y;
      for(y in arr) {
        var image;
        if(arr[y].status=="FileUploading")
          image="fileloader"
        else if (arr[y].status=="Running")
          image="running"
        else 
          image=""
         
         $('.job-list-table').append("<tr id='"+arr[y].jobName+"'><td id='user-id'>"+arr[y].uId+"</td><td>"+arr[y].jobName+"</td>"
              + "<td id='job-type'>"+arr[y].jobType+"</td><td id='parent-name'>"+arr[y].parentInfo+"</td>"
              +"<td id='status'>"+arr[y].status+"<div class='"+image+"'></div>"+"</td><td id='date'>"+arr[y].date+"</td><td> <button type='button' class='btn btn-info'>View Result</button></td></tr>");
      }      
    }               
  });  
});
$(document).on("click", "#buttonman", function(){
  $(this).parents('tr').remove();
});


