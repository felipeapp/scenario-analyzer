<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<style>
.listagem th{
	font-weight: bold; 
}
</style>
<f:view>
	<h2 class="tituloPagina">Confirma��o das informa��es</h2>
	<h:form id="form">
	<table class="listagem" width="100%">
		<caption>Dados da Inscri��o</caption>
		<tbody>
			<tr>
				<th>�rea de Conhecimento:</th>
				<td><h:outputText
					value="#{inscricaoVestibular.obj.opcoesCurso[0].curso.areaVestibular.descricao}" /></td>
			</tr>
			<tr>
				<th>Primeira Op��o:</th>
				<td><h:outputText
					value="#{inscricaoVestibular.obj.opcoesCurso[0].curso.municipio}" />
				- <h:outputText
					value="#{inscricaoVestibular.obj.opcoesCurso[0]}" /></td>
			</tr>
			<tr>
				<th>Segunda Op��o:</th>
				<td><h:outputText
					value="#{inscricaoVestibular.obj.opcoesCurso[1].curso.municipio}" />
				- <h:outputText
					value="#{inscricaoVestibular.obj.opcoesCurso[1]}" /></td>
			</tr>
			<tr>
				<th>L�ngua Estrangeira:</th>
				<td><h:outputText
					value="#{inscricaoVestibular.obj.linguaEstrangeira.denominacao}" /></td>
			</tr>
			<tr>
				<th>Regi�o Preferencial de Prova:</th>
				<td><h:outputText
					value="#{inscricaoVestibular.obj.regiaoPreferencialProva.denominacao}" /></td>
			</tr>
			<c:if test="${inscricaoVestibular.obj.processoSeletivo.opcaoBeneficioInclusao}">
				<tr>
					<th><b>Optou pelo Argumento de Inclus�o:</b></th>
					<td>
						<h:outputText value="SIM. O candidato concorrer� COM o benef�cio do Argumento de Inclus�o." rendered="#{inscricaoVestibular.obj.optouBeneficioInclusao}" />
						<h:outputText value="N�O. O candidato concorrer� SEM o benef�cio do Argumento de Inclus�o." rendered="#{!inscricaoVestibular.obj.optouBeneficioInclusao}" />
					</td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4" align="center">
					<h:commandButton value="Confimar a Inscri��o" action="#{inscricaoVestibular.cadastrar}" id="confirmar"/> 
					<h:commandButton value="<< Alterar Op��es" action="#{inscricaoVestibular.editarOpcoesCurso }" id="alterarOpcoes"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{inscricaoVestibular.cancelar}" immediate="true" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp"%>