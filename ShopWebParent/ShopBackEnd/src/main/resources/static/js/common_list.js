function clearFilter() {
	window.location = modulURL;
}

function showDeleteConfirmModal(link, entityName) {
	entityId = link.attr("entityId");
	
	$("#yesButton").attr("href", link.attr("href"));
	$("#confirmText").text(`이 ${entityName}을(를) 삭제하시겠습니까? (ID : ${entityId})`);
	
	$("#confirmModal").modal();
}