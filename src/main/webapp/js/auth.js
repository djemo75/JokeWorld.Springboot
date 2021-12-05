// Execute it when you are authenticated
const getProfile = () => {
	$.ajax({
		url: "/auth/profile",
		method: "GET",
		contentType: "application/json; charset=utf-8",
		success: (data) => {
			localStorage.setItem("user", JSON.stringify(data));
			const userData= JSON.parse(localStorage.getItem("user"));
	    	$("#welcome-message").text(`Hello ${userData.username}`);
		},
		error: (error) => {
			if(error.status===401){
				localStorage.removeItem("user");
				window.location.href="login.html"; // Redirect to login page
			}
		}
	});
}

const logout = () => {
	$.ajax({
		url: "/auth/logout",
		method: "POST",
		contentType: "application/json; charset=utf-8",
		success: (data) => {
			localStorage.removeItem("user");
			window.location.href="login.html"
		},
		error: (error) => {
			alert(error);
		}
	});
}

$( document ).ready(() => {
	// Add listeners when DOM is ready
	$('#close-account-btn').click(e=>{
		e.preventDefault();
		
		const userData= JSON.parse(localStorage.getItem("user"));
		$.ajax({
			url: "/auth/close-account?id="+userData.id,
			method: "DELETE",
			contentType: "application/json; charset=utf-8",
			success: () => {
				localStorage.removeItem("user");
				window.location.href="login.html"
			},
			error: (error) => {
				if(typeof error.responseJSON.message==="string"){
					alert(error.responseJSON.message);
				}
			}
		});
	
	})
});