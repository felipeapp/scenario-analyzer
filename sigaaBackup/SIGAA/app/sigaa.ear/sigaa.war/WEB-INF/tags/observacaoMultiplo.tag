<%@tag import="br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma"%>
<%@tag import="java.util.Iterator"%>
<%@tag import="br.ufrn.sigaa.ensino.dominio.DocenteTurma"%>

<%@ attribute name="aval" required="true" type="br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional" %>
<%@ attribute name="t" required="true"  type="br.ufrn.sigaa.ensino.dominio.Turma" %>
<%@ attribute name="codigo" required="true"  type="java.lang.String" %>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean" %>
<%@ attribute name="maxLength" required="false" type="java.lang.Integer" %>

<%
if (maxLength == null) maxLength = 600;
ObservacoesDocenteTurma obs = aval.getObservacaoMultipla(t.getId());
int dtid = -1;
boolean check = false;
String style = "style=\"display: none;\"";
int restante = maxLength;
if (obs != null && obs.getObservacoes() != null && !obs.getObservacoes().isEmpty()) {
	if (obs.getDocenteTurma() != null)
		dtid = obs.getDocenteTurma().getId();
	style = "";
	check = true;
	restante = maxLength - obs.getObservacoes().length();
	if (restante < 0) restante = 0;
}
if (readOnly == null) readOnly = false;
String textFieldId="text_" + ((int) (Math.random() * Integer.MAX_VALUE));
%>


<input type="checkbox" <%= check ? "checked=\"checked\"" : "" %> onclick="if (this.checked) { $('obs_tm_${ t.id }').style.display = '' } else { $('obs_tm_${ t.id }').style.display = 'none';  $(<%=textFieldId%>).value = '';  }" <%=readOnly?"disabled":"" %>/> 
  <%= codigo %> - 
  <% for(Iterator it = t.getDocentesTurmas().iterator(); it.hasNext(); ) {
	 DocenteTurma dt = (DocenteTurma) it.next(); %>
	 <%= it.hasNext() ? dt.getDocenteNome() + ", " : dt.getDocenteNome() %>
  <% }%>
<br/>
<div id="obs_tm_${ t.id }" <%= style %>>
O seu coment&aacute;rio refere-se a algum professor espec&iacute;fico? <select name="opcao_tmd_${ t.id }" style="min-width: 120px" <%=readOnly?"disabled":"" %> >
<option value="-1" <%= dtid == -1 ? "selected=\"selected\"" : "" %>>Nenhum</option>
<% for(Iterator it = t.getDocentesTurmas().iterator(); it.hasNext(); ) {
	DocenteTurma dt = (DocenteTurma) it.next();
%>
<option value="<%= dt.getId() %>" <%= dtid == dt.getId() ? "selected=\"selected\"" : "" %>><%= dt.getDocenteNome() %></option>
<% } %>
<option value="-1"  <%= dtid == -1 ? "selected=\"selected\"" : "" %>>Todos</option>
</select>
<br/>
<textarea name="obs_tmd_${ t.id }" rows="4" cols="105" <%=readOnly?"disabled":"" %> id="<%=textFieldId%>" 

  onkeyup="if (this.value.length > <%=maxLength%>) this.value = this.value.substring(0, <%=maxLength%>); $(<%=textFieldId+"_count"%>).value = <%=maxLength%> - this.value.length;" 
onkeydown="if (this.value.length > <%=maxLength%>) this.value = this.value.substring(0, <%=maxLength%>); $(<%=textFieldId+"_count"%>).value = <%=maxLength%> - this.value.length;"><%= obs != null ? obs.getObservacoes() : "" %></textarea>
<br/>
Você pode digitar <input readonly type="text" id="<%=textFieldId +"_count"%>" size="3" value="<%=restante%>"> caracteres.
</div>