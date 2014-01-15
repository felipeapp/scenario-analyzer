<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cancelar Banca</h2>
	
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Bancas do discente</h2>

	<c:set value="#{bancaPos.discente}" var="discente"></c:set>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<h:form id="formulario">
	
	<table class="formulario" width="650px">
			
			<tr>
				<th class="obrigatorio">Justificativa:</th>
				<td>
					<h:inputTextarea id="observacao" value="#{bancaPos.obj.observacao}" rows="2" cols="70"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar Banca" action="#{bancaPos.removerBanca}" id="btnChamada" /> 
						<h:commandButton value="Cancelar" action="#{bancaPos.listar}" 
							onclick="#{confirm}" immediate="true" id="btnCancelar" />
					</td>
				</tr>
			</tfoot>
	</table>
	

	
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
