<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table tbody tr th, table th { font-weight: bold;}
</style>

<f:view>
 <h2><ufrn:subSistema /> &gt; Proposta Submetida</h2>
  <h:form id="form">
   <table class=formulario width="100%">
   	<caption class="listagem">Minha Proposta</caption>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
			<%@include file="include/view_dados_gerais.jsp"%>
		</table>
	  </td>
	 </tr>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
			<%@include file="include/view_coordenacao.jsp"%>
		</table>
	  </td>
	 </tr>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
			<%@include file="include/view_objetivos.jsp"%>
		</table>
	  </td>
	 </tr>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
			<%@include file="include/view_processo_seletivo.jsp"%>
		</table>
	  </td>
	 </tr>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
			<%@include file="include/view_corpo_docente.jsp"%>
		</table>
	  </td>
	 </tr>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
			<%@include file="include/view_disciplinas.jsp"%>
		</table>
	  </td>
	 </tr>
   	<tfoot>
	  <tr>
		<td colspan="4" style="text-align: center;">
   			<table class="subFormulario" width="100%">
			  <tr>
				<c:choose>
					<c:when test="${ cursoLatoMBean.obj.selecionado }">
						<td align="center">
							<input type="button" value="<< Voltar" onclick="history.go(-1)">
						</td>
					</c:when>
					<c:otherwise>
						<td align="center">
							<h:commandButton value="#{cursoLatoMBean.confirmButton}" action="#{cursoLatoMBean.concluir}" id="submeter" />
							<h:commandButton value="<< Alterar Dados Gerais" action="#{cursoLatoMBean.dadosGerais}" id="dadosGerais" />
							<h:commandButton value="<< Alterar Disciplina" action="#{cursoLatoMBean.telaAnterior}" id="disciplina" />
							<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
						</td>
					</c:otherwise>
				</c:choose>
			   </tr>
			</table>
	   </tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>