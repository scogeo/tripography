<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="v" uri="http://rumbleware.com/jsp/tags/validate" %>
<fmt:setBundle basename="com.fitunity.web.controller.Signup"/>
<v:form id="settingsForm" method="post" action="/settings/password/update" modelAttribute="settings">
    <div class="formField clearfix">
        <sf:label path="currentPassword" class="grid_3 grid_first">Current Password</sf:label>
        <div class="grid_3">
            <v:input path="currentPassword" type="password"/>
        </div>
    </div>
    <div class="formField clearfix">
        <sf:label class="grid_3 grid_first" path="newPassword">New Password</sf:label>
        <div class="grid_3">
            <v:input path="newPassword" type="password"/>
        </div>
    </div>
    <div class="formField clearfix">
        <sf:label class="grid_3 grid_first" path="confirmPassword">Confirm New Password</sf:label>
        <div class="grid_3">
            <v:input path="confirmPassword" type="password"/>
        </div>
    </div>
    <div class="actions clearfix">
        <div class="grid_3 prefix_3 suffix_3">
            <button type="submit" value="save">Update</button>
        </div>
    </div>

</v:form>