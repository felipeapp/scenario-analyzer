<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	tr.header td {font-weight: bold;}
</style>

<f:view>
	<h2><ufrn:subSistema /> &gt; Despacho da Proposta</h2>
		<h:form id="form">
  		 <table class="formulario" width="90%">
	  		<caption>Despacho</caption>
	  		  <tbody>
				<tr>
					<td width="100px;">Situação Atual:</td>
					<td>${cursoLatoMBean.obj.propostaCurso.situacaoProposta.descricao}</td>
				</tr>
			 </tbody>
				<tr>
					<td>Despacho:</td>
					<td>
						<h:inputTextarea id="observacao" value="#{cursoLatoMBean.obj.propostaCurso.historico.observacoes}" style="width: 95%" rows="10" 
						readonly="true"/>
					</td>
				</tr>
		 		<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton action="#{cursoLatoMBean.listar}" value="<< Voltar" id="buttonVoltar" />
						</td>
					</tr>
				</tfoot>
		 </table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>