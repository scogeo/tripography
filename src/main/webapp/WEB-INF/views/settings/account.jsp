<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="v" uri="http://rumbleware.com/jsp/tags/validate" %>
<fmt:setBundle basename="com.fitunity.web.controller.Signup"/>
<v:form id="settingsForm" method="post" action="/settings/account/update" modelAttribute="settings">
    <div class="formField clearfix">
        <sf:label path="fullname" class="grid_3 grid_first">Name</sf:label>

        <div class="grid_3">
            <v:input path="fullname" type="text"/>
        </div>
    </div>
    <div class="formField clearfix">
        <sf:label class="grid_3 grid_first" path="username">Username</sf:label>
        <div class="grid_3">
            <v:input path="username" type="text"/>
        </div>
    </div>
    <div class="formField clearfix">
        <sf:label class="grid_3 grid_first" path="email">Email</sf:label>
        <div class="grid_3">
            <v:input path="email" type="text"/>
        </div>
    </div>
    <div class="actions clearfix">
        <div class="grid_first grid_3 prefix_3 suffix_3">
            <button class="button blue" type="submit" value="save">Update</button>
        </div>
    </div>

</v:form>