<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Logar como Coordenador de P�lo</h2>

	<div class="descricaoOperacao">
		Selecione um item da lista de usu�rios e clique em <strong>Logar</strong>
		para acessar o sistema com a vis�o do coordenador de p�lo. � poss�vel filtrar a
		lista de coordenadores escolhendo um p�lo na lista de filtros. 
	</div>

	<h:form id="formulario">
	<h:messages showDetail="true"/>
	<br/>

	<table class="formulario" width="70%">
	<caption>Coordenadores de P�lo com usu�rio cadastrado</caption>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Coordenadores de P�lo</caption>
				<tr>
					<th class="required">P�lo:</th>
					<td>
						<h:selectOneMenu value="#{ logarComoCoordPolo.polo.id }" valueChangeListener="#{ logarComoCoordPolo.carregarTutoresPolo }" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="Selecione um P�lo" />
							<f:selectItems value="#{tutoriaAluno.polos}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Coord. P�lo:</th>
					<td>
						<h:selectOneMenu value="#{ logarComoCoordPolo.usuario.id }">
							<f:selectItem itemValue="0" itemLabel="Selecione um Usu�rio" />
							<f:selectItems value="#{ logarComoCoordPolo.coordenadoresPolo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Logar" action="#{ logarComoCoordPolo.logar }"/>
			<h:commandButton value="Cancelar" action="#{ logarComoCoordPolo.cancelar }"/>
		</td>
	</tr>
	</tfoot>
	</table>

	</h:form>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
