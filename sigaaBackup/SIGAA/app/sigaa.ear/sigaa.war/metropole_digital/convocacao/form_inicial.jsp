<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
  
<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<a4j:keepAlive beanName="convocacaoProcessoSeletivoTecnicoIMD"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Cadastrar Convoca��o de Candidatos</h2>
	<div class="descricaoOperacao">
	<p><b>Caro Usu�rio,</b></p>
	<p>Esta opera��o permite realizar a convoca��o dos alunos aprovados na
	ordem de chamada do processo seletivo.</p>
	<p>Informe os valores no formul�rio, <b>clique
	uma �nica vez</b> em "Avan�ar", e aguarde at� o fim
	do processamento.</p>
	
	</div>

	<h:form>

<div align="center">
	<a4j:region id="progressPanel">
		<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
			value="#{ convocacaoProcessoSeletivoTecnicoIMD.percentualProcessado }"
			label ="#{ convocacaoProcessoSeletivoTecnicoIMD.mensagemProgresso }"
			reRenderAfterComplete="progressPanel" ignoreDupResponses="true">
			<f:facet name="initial">
				<a4j:outputPanel>
					<table class="formulario" width="80%">
						<caption>Dados da Convoca��o</caption>
						<tr>
							<th class="obrigatorio" width="40%" align="right">Descri��o da Convoca��o:</th>
							<td  width="60%" align="left">
								<h:inputText id="descricao" value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.descricao}" size="60" maxlength="100"/>
								<ufrn:help>Informe uma descri��o para esta convoca��o, como por exemplo: 1� Chamada</ufrn:help>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio" width="40%" align="right">Processo Seletivo:</th>
							<td width="60%" align="left">
								<h:selectOneMenu id="selectPS" value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.processoSeletivo.id}" valueChangeListener="#{convocacaoProcessoSeletivoTecnicoIMD.carregarVagas}">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems id="listaAnosReferencia" value="#{convocacaoProcessoSeletivoTecnicoIMD.processosCombo}" />
									<a4j:support event="onchange" reRender="totalVagas, totalConvocados, vagasDisponiveis" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio" width="40%" align="right">P�lo / Grupo:</th>
							<td width="60%" align="left">
								<h:selectOneMenu id="opcao" value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.opcao.id}" valueChangeListener="#{convocacaoProcessoSeletivoTecnicoIMD.carregarVagas}">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
									<f:selectItems value="#{ convocacaoProcessoSeletivoTecnicoIMD.polosCombo }" />
									<a4j:support event="onchange" reRender="totalVagas, totalConvocados, vagasDisponiveis" />
								</h:selectOneMenu>
							</td>
						</tr>
						
						<tfoot>
							<tr>
								<td colspan="2">
									<h:commandButton id="btnAvan�ar" value="Avan�ar >> " action="#{convocacaoProcessoSeletivoTecnicoIMD.buscarDadosVagas}" />
									<h:commandButton value="Cancelar" action="#{ convocacaoProcessoSeletivoTecnicoIMD.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					</table>
					<br/>
				</a4j:outputPanel>
			
				
			</f:facet>
			
		</rich:progressBar>
	</a4j:region>
</div>
</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>