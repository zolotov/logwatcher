<html>
<head>
	<meta content="text/html;charset=UTF8"/>
	<meta name="layout" content="main"/>
</head>
<body>

<h1><span id="firstDot">&bull;</span><span id="secondDot">&bull;</span><span id="thirdDot">&bull;</span></h1>

<div id="menuBar">
	<g:each in="${linkDates}">
		<g:set var="href" value="${createLink(controller: 'index', action: 'index', params: [date: it.value])}" />
		<g:if test="${it.value == date}">
			<a href="${href}" class="selected">${it.key}</a>
		</g:if>
		<g:else>
			<a href="${href}">${it.key}</a>
		</g:else>
	</g:each>
</div>

<script type="text/javascript">
	severityLevel = '<%= severity as String %>'
</script>

<div id="mainPanel">
	<h2>Записи за <%= dateAsString %></h2>

	<div id="sliderContainer">
		<div id="sliderValue"></div>
		<div id="slider"></div>
	</div>

	<g:if test="${entries.size() > 0}">
		<g:each in="${entries}" var="entry">
			<f:entry ref="${entry}"/>
		</g:each>
		<div id="footer">
			Всего <%= entries.collect { it.count }.sum().pluralize("сообщение сообщения сообщений") %>,
			<%= entries.size().pluralize("запись записи записей") %>
		</div>
	</g:if>
	<g:else>
		<div id="notificationPanel">
			Плохо дело. Нет записей.
		</div>
	</g:else>
</div>
</body>
</html>
