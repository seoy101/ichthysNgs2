/**
 * 
 */
var conf = new Object(); 
function contentChange(content){
  $.ajax({
    url:'http://localhost:9000/views/'+content,
    type:'GET',
    success: function(data) {
      $("#content").html(data);
      if(content=="jobCreate"){
        jobNameValidation();
      }
    }
 });
}

////////////////websocket//////////////
var websocket=null;
function wsConnect() {
  var wsUri = "ws://localhost:9000/websocket";  
  websocketcallback(wsUri);
  
}
function websocketcallback(wsUri) {
  
  websocket = new WebSocket(wsUri);
  websocket.onopen = function(evt) { onOpen(evt) };
  websocket.onclose = function(evt) { onClose(evt) };
  websocket.onmessage = function(evt) { onMessage(evt) };
  websocket.onerror = function(evt) { onError(evt) };
}

function onOpen(evt) {
  doSend(conf.uId);
}

function onClose(evt) {

}

function onMessage(evt)
{
  var image;
  var wsJson = JSON.parse(evt.data);
  if(wsJson.status=="FileUploading")
    image ="fileloader";
  else if(wsJson.status=="Running")
    image = "running";
  else
    image= "";
  $("#"+wsJson.jobName+" #status").html(wsJson.status+"<div class='"+image+"'</div>");
}

function onError(evt) {
//  alert("error");
//  alert(evt.data);
}

function doSend(message) {
  websocket.send(message);
}


$(document).ready(function(){
  conf.uId=$("#uId").val();
  contentChange("jobCreate");
  wsConnect();
});