<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO } %>">

<f:view>
	<a4j:keepAlive beanName="vincularUnidadeOrcamentariaMBean" />
	
	<h2><ufrn:subSistema /> > Vincular Unidade Orçamentária à Ação de Extensão</h2>

	<div class="descricaoOperacao">
       Através desta página o usuário poderá vincular a ação selecionada a uma Unidade Orçamentária. Selecione uma unidade e confirme a operação clicando no botão 'Vincular Unidade ao Projeto'. 
    </div>
	
	
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Vincular Unidade Orçamentária</caption>

			<tr>
				<th width="18%" class="rotulo">Nº Institucional:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.numeroInstitucional}" />/<h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th width="18%" class="rotulo">Título:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Ano:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Período:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.dataInicio}" /> até <h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.dataFim}" /></td>
			</tr>

            <tr>
                <th class="rotulo">Unidade Gestora:</th>
                <td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.unidade.nome}" /></td>
            </tr>


			<tr>
				<th class="rotulo">Coordenador(a):</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.coordenador.pessoa.nome}" /></td>
			</tr>

            <tr>
                <th class="rotulo">Situação:</th>
                <td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.situacaoProjeto.descricao}" /></td>
            </tr>


			<tr>
				<th class="required">Unidade Orçamentária:</th>
				<td><h:selectOneMenu value="#{vincularUnidadeOrcamentariaMBean.obj.unidadeOrcamentaria.id}" id="selectUnidadeOrcamentaria" >
				    <f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
					<f:selectItems value="#{unidade.unidadesOrcamentariasExtensaoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<td><br /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Vincular Unidade ao Projeto" action="#{vincularUnidadeOrcamentariaMBean.confirmarVinculo}" id="btConfirmarVincularUnidade"/> 
					    <h:commandButton value="<< Voltar" action="#{vincularUnidadeOrcamentariaMBean.listar}" id="btVoltar"/>
						<h:commandButton value="Cancelar" action="#{vincularUnidadeOrcamentariaMBean.cancelar}" onclick="#{confirm}" id="btCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		  <br />
        <center><h:graphicImage url="/img/required.gif"    style="vertical-align: top;" /> <span class="fontePequena">
            Campos de preenchimento obrigatório. </span></center>
        <br />
		
	</h:form>
</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>