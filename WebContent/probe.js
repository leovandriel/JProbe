function doPost(action, map) {
	var form = document.createElement('form');
	form.setAttribute('action', action);
	form.setAttribute('method', 'post');
	form.innerHTML = '';
	for ( var name in map) {
		form.innerHTML += '<input type="hidden" name="' + name + '" value="'
				+ map[name] + '" />';
	}
	document.body.appendChild(form);
	form.submit();
	return false;
};