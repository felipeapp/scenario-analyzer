
<%@tag import="br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma"%>
<%@ attribute name="aval" required="true" type="br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional" %>
<%@ attribute name="dtId" required="true"  type="java.lang.Integer" %>
<%@ attribute name="codigo" required="true"  type="java.lang.String" %>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean" %>
<%@ attribute name="maxLength" required="false" type="java.lang.Integer" %>

<%
if (maxLength == null) maxLength = 600;
ObservacaoAvaliacaoInstitucional obs;
if (aval.getFormulario() != null && aval.getFormulario().isAvaliacaoDocenciaAssistida()) {
	obs = aval.getObservacaoDocenciaAssistida(dtId);
} else {
	obs = aval.getObservacaoSimples(dtId);
}
boolean check = false;
int restante = maxLength;
String style = "style=\"display: none;\"";
if (obs != null && obs.getObservacoes() != null && !obs.getObservacoes().isEmpty()) {
	style = "";
	check = true;
	restante = maxLength - obs.getObservacoes().length();
	if (restante < 0) restante = 0;
}
if (readOnly == null) readOnly = false;
String textFieldId="text_" + ((int) (Math.random() * Integer.MAX_VALUE));
%>

<%@tag import="br.ufrn.sigaa.avaliacao.dominio.ObservacaoAvaliacaoInstitucional"%><input type="checkbox" <%= check ? "checked=\"checked\"" : "" %> onclick="if (this.checked) { $('obs_ts_${ dtId }').style.display = '' } else { $('obs_ts_${ dtId }').style.display = 'none';  $('obs_${ dtId }').value = '';  }" <%=readOnly?"disabled":"" %>/> <%= codigo %><br/>
<div id="obs_ts_${ dtId }" <%= style %>>
<textarea name="obs_${ dtId }" id="obs_${ dtId }" rows="4" cols="105" <%=readOnly?"disabled":"" %> id="<%=textFieldId%>" 
  onkeyup="if (this.value.length > <%=maxLength%>) this.value = this.value.substring(0, <%=maxLength%>); $(<%=textFieldId+"_count"%>).value = <%=maxLength%> - this.value.length;" 
onkeydown="if (this.value.length > <%=maxLength%>) this.value = this.value.substring(0, <%=maxLength%>); $(<%=textFieldId+"_count"%>).value = <%=maxLength%> - this.value.length;"><%= obs != null ? obs.getObservacoes() : "" %></textarea>
<br/>
Você pode digitar <input readonly type="text" id="<%=textFieldId +"_count"%>" size="3" value="<%=restante%>"> caracteres.
</div>