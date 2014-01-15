
<%@ tag import="br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento"%>
<%@ attribute name="tid" required="true"  type="java.lang.Integer" %>
<%@ attribute name="aval" required="true" type="br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional" %>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean" %>
<%@ attribute name="maxLength" required="false" type="java.lang.Integer" %>

<%
	if (maxLength == null) maxLength = 600;
	ObservacoesTrancamento ot = aval.getTrancamento(tid);
	if (readOnly == null) readOnly = false;
	String textFieldId="text_" + ((int) (Math.random() * Integer.MAX_VALUE));
	int restante = maxLength - (ot == null || ot.getObservacoes() == null  ? 0 : ot.getObservacoes().length());
	if (restante < 0) restante = 0;
%>

<textarea name="tr_${ tid }" cols="80" <%=readOnly?"disabled":"" %> 
  onkeyup="if (this.value.length > <%=maxLength%>) this.value = this.value.substring(0, <%=maxLength%>); $(<%=textFieldId+"_count"%>).value = <%=maxLength%> - this.value.length;"
onkeydown="if (this.value.length > <%=maxLength%>) this.value = this.value.substring(0, <%=maxLength%>); $(<%=textFieldId+"_count"%>).value = <%=maxLength%> - this.value.length;" ><%= (ot != null ? ot.getObservacoes() : "") %></textarea>
<br/>
Você pode digitar <input readonly type="text" id="<%=textFieldId +"_count"%>" size="3" value="<%=restante%>"> caracteres.
