<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="v" uri="http://rumbleware.com/jsp/tags/validate" %>
<fmt:setBundle basename="com.fitunity.web.controller.Signup"/>
<v:form id="settingsForm" method="post" enctype="multipart/form-data" action="/settings/profile/update" modelAttribute="settings">
    <div class="formField clearfix">
        <label for="image" class="grid_2 grid_first">Picture</label>

        <div class="grid_3">
            <input id="image" name="image" type="file"/>
        </div>
    </div>
    <div class="formField clearfix">
        <sf:label class="grid_2 grid_first" path="location">Location</sf:label>
        <div class="grid_3">
            <v:input path="location" type="text"/>
        </div>
    </div>
    <div class="formField clearfix">
        <sf:label class="grid_2 grid_first" path="bio">Bio</sf:label>
        <div class="grid_3">
            <v:input path="bio" type="text"/>
        </div>
    </div>
    <div class="actions">
        <button class="grid_3 prefix_2" type="submit" value="save">Save</button>
    </div>

</v:form>