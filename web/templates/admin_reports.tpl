{include file="doclet_usage.tpl"}

{* Database Statistics *}
<h3>Statistics</h3>

<h4>Users</h4>
Number of users: {$stats.users}<br/>
Number of professors: {$stats.profs}<br/>
Number of administrators: {$stats.admin}<br/>

<h4>Schools</h4>
Number of schools: {$stats.schools}<br/>

<h4>Courses</h4>
Number of courses: {$stats.courses}<br/>
Number of enabled courses: {$stats.enabledCourses}<br/>
Number of disabled courses: {$stats.disabledCourses}<br/>
Number of students enrolled in courses: {$stats.enrollments}<br/>

<h4>Assignments</h4>
Number of assignments: {$stats.assignments}<br/>
Average number of assignments per course: {if $stats.courses > 0}{$stats.assignments/$stats.courses}{else}N/A{/if}<br/>    

<h4>Reports</h4>
Number of doclets: {$stats.doclets}<br/>
Number of submissions: {$stats.reports}<br/>
Number of files: {$stats.files}<br/>
