<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO } %>">

<f:view>
	<a4j:keepAlive beanName="vincularUnidadeOrcamentariaMBean" />
	
	<h2><ufrn:subSistema /> > Vincular Unidade Or�ament�ria � A��o de Extens�o</h2>

	<div class="descricaoOperacao">
       Atrav�s desta p�gina o usu�rio poder� vincular a a��o selecionada a uma Unidade Or�ament�ria. Selecione uma unidade e confirme a opera��o clicando no bot�o 'Vincular Unidade ao Projeto'. 
    </div>
	
	
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Vincular Unidade Or�ament�ria</caption>

			<tr>
				<th width="18%" class="rotulo">N� Institucional:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.numeroInstitucional}" />/<h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th width="18%" class="rotulo">T�tulo:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Ano:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Per�odo:</th>
				<td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.dataInicio}" /> at� <h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.dataFim}" /></td>
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
                <th class="rotulo">Situa��o:</th>
                <td><h:outputText value="#{vincularUnidadeOrcamentariaMBean.obj.situacaoProjeto.descricao}" /></td>
            </tr>


			<tr>
				<th class="required">Unidade Or�ament�ria:</th>
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
            Campos de preenchimento obrigat�rio. </span></center>
        <br />
		
	</h:form>
</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>