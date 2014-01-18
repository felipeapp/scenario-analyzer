<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%-- CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.GESTOR_CONVOCACOES_VESTIBULAR} ); --%>
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style>  
<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<a4j:keepAlive beanName="convocacaoProcessoSeletivoTecnicoIMD"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Cadastrar Convoca��o de Candidatos</h2>
	<div class="descricaoOperacao">
	<p><b>Caro Usu�rio,</b></p>
	<p>Esta opera��o permite realizar a convoca��o dos alunos aprovados na
	ordem de chamada do processo seletivo.</p>
	<p>Informe os valores no formul�rio, <b>clique
	uma �nica vez</b> em "Cadastrar Convoca��o", e aguarde at� o fim
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
							<th align="right"><b>Descri��o da Convoca��o:</b></th>
							<td align="left">
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.descricao}"/>
							</td>
						</tr>
						<tr>
							<th width="40%" align="right"><b>Processo Seletivo:</b></th>
							<td align="left">
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.psSelecionado.nome}"/>
							</td>
						</tr>
						<tr>
							<th align="right"><b>P�lo / Grupo:</b></th>
							<td align="left">
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.opcaoSelecionada.descricao}"/>
							</td>
						</tr>
											
						<tr>
							<th align="right"><b>TOTAL DE VAGAS NO P�LO: </b></th>
							<td align="left">
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.qtdVagasOpcaoSelecionada}"/>
							</td>
						</tr>
						
						<tr>
							<th align="right"><b>TOTAL DE CONVOCADOS NO P�LO: </b></th>
							<td align="left">
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.qtdConvocadosOpcaoSelecionada}"/>
							</td>
						</tr>
						
						<tr>
							<th align="right"><b>TOTAL DE CANDIDATOS EXCLU�DOS NO P�LO: </b></th>
							<td align="left">
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.qtdExcluidos}"/>
							</td>
						</tr>
						
						<tr>
							<th align="right"><b>VAGAS DISPON�VEIS NO P�LO: </b></th>
							<td align="left">
								<b style="color: green"><h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.qtdDisponiveis}" /></b>
							</td>
						</tr>
						
						<tr>
							<th align="right"><b>TOTAL DE SUPLENTES NO P�LO - GRUPO: </b></th>
							<td align="left">
								<b  style="color: blue"><h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.qtdSuplentePolo}" /></b>
							</td>
						</tr>
						
						<tr>
							<th class="obrigatorio" align="right">Quantidade de discentes a serem convocados:</th>
							<td align="left">
								<h:inputText id="qtdConvocacao" value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.quantidadeDiscentesSemReserva}" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" size="5" maxlength="5" /> 
							</td>
						</tr>
						<tr>
							<th width="25%" align="right">Grupo de Reserva de Vagas: </th>
							<td align="left">
								<h:selectOneMenu id="selectGrupo" value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.grupo.id}">
									<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
									<f:selectItems value="#{convocacaoProcessoSeletivoTecnicoIMD.gruposCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						
						
						
						<tfoot>
							<tr>
								<td colspan="2">
									<a4j:commandButton id="btnBuscar" value="Cadastrar Convoca��o" action="#{convocacaoProcessoSeletivoTecnicoIMD.cadastrarConvocacoesImportacaoTecnicoNew}" reRender="progressPanel" onclick="this.disabled=true; this.value='Por favor, aguarde...'"/>
									<h:commandButton value="<< Voltar" action="#{ convocacaoProcessoSeletivoTecnicoIMD.voltarFormInicial }" id="btnVoltar"/>
									<h:commandButton value="Cancelar" action="#{ convocacaoProcessoSeletivoTecnicoIMD.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					</table>
					<br/>
				</a4j:outputPanel>
			
				
			</f:facet>
			<f:facet name="complete">
	        	<br/>
	        	<h:outputText>
	        		<br/><br/>
	            	<table class="formulario" width="80%">
						<caption>
							<c:if test="${convocacaoProcessoSeletivoTecnicoIMD.errosConvocacao == null}">Ocorreu um problema no processamento das notas.</c:if>
	        				<c:if test="${convocacaoProcessoSeletivoTecnicoIMD.errosConvocacao != null}">Processamento Conclu�do.</c:if>
						</caption>
						<tr>
							<th class="rotulo" width="25%">Processo Seletivo / Vestibular:</th>
							<td>
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.processoSeletivo.nome}"/>
							</td>
						</tr>
						<tr>
							<th class="rotulo">Descri��o:</th>
							<td>
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.descricao}" />
							</td>
						</tr>
						<tr>
							<th class="rotulo">Data da Convoca��o:</th>
							<td>
								<h:outputText value="#{convocacaoProcessoSeletivoTecnicoIMD.obj.dataConvocacao}"/>
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2">
									<h:commandButton value="Cancelar" action="#{ convocacaoProcessoSeletivoTecnicoIMD.cancelar }" id="btnCancelar2" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					</table>
	            </h:outputText>
	        </f:facet>
		</rich:progressBar>
	</a4j:region>
</div>
</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>