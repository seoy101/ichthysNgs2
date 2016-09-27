var setWindowSize = function(windowSizeArray) {        
    
    $('.container').css({        
        "width"  : windowSizeArray[0],
        "height" : windowSizeArray[1]            
    });
};

var getWindowSize = function() {
    var height = window.innerHeight;
    var width = window.innerWidth;
    
    return [width, height];
};




$(function() {
    setWindowSize(getWindowSize());    
});

var resizeFunction = function() {   
           
    $(window).resize(function() {
        setWindowSize(getWindowSize());   
    });   
};

var placeHolder = function() {   
    
    $(".holder + input").keyup(function() {
        if($(this).val().length) {
            $(this).prev('.holder').hide();
        } else {
            $(this).prev('.holder').show();
        }
    });
    $(".holder").click(function() {
        $(this).next().focus();
    });
};

var loginValidation = function() {
    $('#login-button').click(function() {
        var flagNum = 0;
    	
    	 var id = $('#login-id-input').val();
        var pwd = $('#login-pwd-input').val();
        
        if(id.length === 0) {
            $('#login-email-vali').html("email을 입력하세요");
            flagNum++;
        } else {
            var regex=/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
            if(regex.test(id) === false) {
                $('#login-email-vali').html("email을 다시 입력하세요");
                flagNum++;
            }           
        }
        
        if(pwd.length === 0) {
            $('#login-pwd-vali').html("비밀번호를 입력하세요");
            flagNum++;
        } else {}    
        
        
        if(flagNum === 0) {
        	var obj = new Object();
        	obj.type = "login";
        	obj.id = id;
        	obj.pwd = pwd;
        	//var jsonString = JSON.stringify(obj);
        	
        	$.ajax({
        		type: 'POST' ,
        		url: "http://localhost:9000/login" ,
        		data: obj ,
        		success: function(e) {
        			$('#login-id-input').val("");
        	       $('#login-pwd-input').val("");
        	       if(e === "success") {
        	    	   window.location.replace("http://localhost:9000/main");
        	       } else {
        	    	   $('#login-email-vali').html(e);
        	       }        	       
        		}    						
        	});   	        	
        } else {       	
        }
    });
    
    
};





var signinValidation = function() {
    
    var mailValidation = 0;
	
	$('#signin-id-input').focusout(function() {
		
		mailValidation = 0;
		
		
		var signin_id_Value = $('#signin-id-input').val();
		
		$.ajax({
    		type: 'POST' ,
    		url: "http://localhost:9000/emaildupcheck" ,
    		data: {"email" : signin_id_Value} ,
    		success: function(e) {
    			
    			if(e === "0") {
    				$('#signin-email-vali').html("you can use");
    				
    			} else {
    				$('#signin-email-vali').html("duplicated email");
    				mailValidation = 1;
    				
    			}
    			
    			
    			
    		}    						
    	});   	
	});
	
	
    $('#signin-button').click(function() {
    	var flagVal = 0;
    	
        var id = $('#signin-id-input').val();
        var pwd = $('#signin-pwd-input').val();
        var repwd = $('#signin-repwd-input').val();
        
        if(id.length === 0) {
            $('#signin-email-vali').html("email을 입력하세요");
            flagVal++;
        } else {
            var regex=/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
            if(regex.test(id) === false) {
                $('#signin-email-vali').html("email을 다시 입력하세요");
                flagVal++;
            }           
        }
        
        if(pwd.length === 0) {
            $('#signin-pwd-vali').html("비밀번호를 입력하세요");
            flagVal++;
        } else {}
        
        if(repwd.length === 0) {
            $('#signin-repwd-vali').html("위에서 설정한 비밀번호를 입력하세요");
            flagVal++;
        } else {}
        
        if(pwd !== repwd) {
            $('#signin-repwd-vali').html("위에서 설정한 비밀번호와 다릅니다");
            flagVal++;
        } 
        if(flagVal === 0 && mailValidation === 0) {
        	alert("good");
        	var obj = new Object();
        	obj.type = "signin";
        	obj.id = id;
        	obj.pwd = pwd;
        	var jsonString = JSON.stringify(obj);
        	
        	$.ajax({
        		type: 'POST' ,
        		url: "http://localhost:9000/login" ,
        		data: obj ,
        		success: function(e) {
        			$('#signin-id-input').val("");
        			$('#signin-pwd-input').val("");
        			$('#signin-repwd-input').val("");
        			window.location.replace("http://localhost:9000/main");
        		}    						
        	});   	
        	
        } else {
        	
        	alert("no");
        }
    });
    
    
};


$(document).ready(signinValidation);
$(document).ready(loginValidation);
$(document).ready(placeHolder);
$(document).ready(resizeFunction);
/*
$(function() {
	$('#login-id-input').val(" ");
	$('#login-pwd-input').val("");
	
});*/
