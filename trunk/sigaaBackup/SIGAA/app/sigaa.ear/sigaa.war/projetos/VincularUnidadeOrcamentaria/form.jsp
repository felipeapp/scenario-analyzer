<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.MEMBRO_COMITE_INTEGRADO } %>">

<f:view>
	<a4j:keepAlive beanName="alteracaoProjetoMBean" />
	
	<h2><ufrn:subSistema /> > Vincular Unidade Orçamentária ao Projeto</h2>
	<br>
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Vincular Unidade Orçamentária</caption>

			<tr>
				<th width="18%">Nº Institucional:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.numeroInstitucional}" />/<h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th width="18%">Título:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th>Ano:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th>Período:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.dataInicio}" /> até <h:outputText value="#{alteracaoProjetoMBean.obj.dataFim}" /></td>
			</tr>

            <tr>
                <th>Unidade Gestora:</th>
                <td><h:outputText value="#{alteracaoProjetoMBean.obj.unidade.nome}" /></td>
            </tr>


			<tr>
				<th>Coordenador(a):</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.coordenador.pessoa.nome}" /></td>
			</tr>

            <tr>
                <th>Situação:</th>
                <td><h:outputText value="#{alteracaoProjetoMBean.obj.situacaoProjeto.descricao}" /></td>
            </tr>


			<tr>
				<th class="required">Unidade Orçamentária:</th>
				<td><h:selectOneMenu value="#{alteracaoProjetoMBean.obj.unidadeOrcamentaria.id}" id="selectUnidadeOrcamentaria" >
				    <f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
					<f:selectItems value="#{unidade.allUnidadesOrcamentariasCombo}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<td><br /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Vincular Unidade ao Projeto" action="#{alteracaoProjetoMBean.vincularUnidadeOrcamentaria}" rendered="#{acesso.comissaoIntegrada}" id="btConfirmarVincularUnidade"/> 
						<h:commandButton value="Cancelar" action="#{alteracaoProjetoMBean.cancelar}" onclick="#{confirm}" id="btCancelar"/>
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