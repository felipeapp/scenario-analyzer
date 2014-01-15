<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Indicar Discente do Plano de Trabalho</h2>


<h:form id="form">

<table class="formulario" width="90%">
<caption>Indicar Discente</caption>
<tbody>
	<tr>
		<th class="rotulo" width="21%"> Título: </th>
		<td colspan="2"> 
			<h:outputText value="#{planoTrabalhoProjeto.obj.projeto.anoTitulo}" />
		</td>
	</tr>
	<tr>
		<th class="rotulo"> Coordenador(a): </th>
		<td colspan="2">
			<h:outputText value="#{planoTrabalhoProjeto.obj.projeto.coordenador.pessoa.nome }" />
		</td>
	</tr>

	<tr>
		<th class="rotulo"> Local de Trabalho: </th>
		<td colspan="2"><ufrn:format type="texto" name="planoTrabalhoProjeto" property="obj.localTrabalho"/></td>
	</tr>

	<tr>
		<td colspan="4" class="subFormulario" style="text-align: center"> 
			<h:outputText value="Finalização" rendered="#{planoTrabalhoProjeto.obj.discenteProjeto != null}" /> 
		</td>
	</tr>


		<tr>
			<th class="rotulo"> 
				<h:outputText value="Discente Anterior" 
				rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior != null}" /></th>
			<td colspan="2">
					<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior.discente.pessoa.nome}" 
						rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior != null}" />
			</td>
		</tr>		
		<tr>
			<th class="rotulo"> 
				<h:outputText value="Motivo da Substituição" 
				rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior != null}" /></th>
			<td colspan="2"> 
				<h:outputText value="#{ planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior.motivoSubstituicao}"
				rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior != null}" /> 
			</td>
		</tr>


		<tr>
			<th class="rotulo"> Discente Atual: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.discente.pessoa.nome}" />
			</td>
		</tr>
		<tr>
			<th class="rotulo">Vínculo: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.tipoVinculo.descricao}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo">Situação: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.situacaoDiscenteProjeto.descricao}"/>
			</td>
		</tr>		
		
		<tr>
			<th> <b>Data de Início:</b> </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.dataInicio}" >
					<f:convertDateTime type="date" dateStyle="medium"/>
				</h:outputText>
			</td>
		</tr>
				
		<tr>
			<th class="required"> Data da Finalização: </th>
			<td colspan="2">
				<t:inputCalendar size="10" value="#{planoTrabalhoProjeto.obj.discenteProjeto.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"  maxlength="10" id="fimDiscente"/>
			</td>
		</tr>
		<tr>
			<th class="required"> Motivo da Substituição:</th>
			<td>
			<h:selectOneMenu value="#{planoTrabalhoProjeto.obj.discenteProjeto.motivoSubstituicao}" id="motivo">
				<f:selectItem itemValue="" itemLabel="-- SELECIONE UM TIPO --" />
				<f:selectItem itemValue="SAÚDE" itemLabel="SAÚDE" />
				<f:selectItem itemValue="VÍNCULO EMPREGATÍCIO" itemLabel="VÍNCULO EMPREGATÍCIO" />
				<f:selectItem itemValue="MUDANÇA DE PROJETO" itemLabel="MUDANÇA DE PROJETO" />
				<f:selectItem itemValue="CONCLUSÃO DA GRADUAÇÃO" itemLabel="CONCLUSÃO DA GRADUAÇÃO" />
				<f:selectItem itemValue="A PEDIDO DO ALUNO" itemLabel="A PEDIDO DO ALUNO" />
				<f:selectItem itemValue="FALECIMENTO" itemLabel="FALECIMENTO" />
				<f:selectItem itemValue="OUTROS" itemLabel="OUTROS" />
			</h:selectOneMenu>
			</td>
		</tr>
		
	<tr>
		<td colspan="4" class="subFormulario" style="text-align: center"> Substituição </td>
	</tr>

	<tr>
		<td colspan="4">
				
			<table style="subFormulario" width="100%">
				<tr>
					<th width="20%" class="required">Discente:</th>
					<td>
						<h:inputHidden id="idDiscente" value="#{ planoTrabalhoProjeto.obj.discenteProjetoNovo.discente.id }"/>
						<h:inputText id="nomeDiscente" value="#{ planoTrabalhoProjeto.obj.discenteProjetoNovo.discente.pessoa.nome }" style="width: 95%"/>
	
						<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..." id="imgIndicadorDiscente"/> </span>
						
					</td>
				</tr>
				
				<tr>
					<th  class="required">Vículo:</th>
					<td>
						<h:selectOneMenu value="#{planoTrabalhoProjeto.obj.discenteProjetoNovo.tipoVinculo.id}" id="tipoVinculoDis">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
										<f:selectItems value="#{planoTrabalhoProjeto.voluntario ? tipoVinculoDiscenteBean.naoRemuneradosAtivosAssociadosCombo : tipoVinculoDiscenteBean.remuneradosAtivosAssociadosCombo}" />
									</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required"> Data de Início: </th>
					<td colspan="2">
						<t:inputCalendar size="10" value="#{planoTrabalhoProjeto.obj.discenteProjetoNovo.dataInicio}" 
						renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"  maxlength="10" id="inicioDiscente"/>
					</td>
				</tr>
				
					<tr>
						<th class="required"> Banco:</th>
						<td>
							<h:selectOneMenu value="#{planoTrabalhoProjeto.obj.discenteProjetoNovo.banco.id}" id="bancoDiscenteProjeto">
								<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM BANCO --"  />
								<f:selectItems value="#{discenteMonitoria.allBancoCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>					
					<tr>
						<th class="required"> Nº Agência: </th>
						<td> <h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjetoNovo.agencia}" size="10" maxlength="15" id="numAgenciaDiscenteProjeto"/>	</td>
					</tr>
					<tr>
						<th class="required"> Nº Conta Corrente: </th>
						<td> <h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjetoNovo.conta}" size="10" maxlength="15" id="numeroContaCorrente"/>	</td>
					</tr>
					<tr>
						<th class="required"> Nº Operação: </th>
						<td> <h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjetoNovo.operacao}" size="5" maxlength="15" id="numeroOperacao"/>	</td>
					</tr>
					<tr>
						<td class="subFormulario" colspan="2">
							<div class="infoAltRem">
								<html:img page="/img/report.png" style="overflow: visible;"/>: Histórico
							</div>
						</td>
					</tr>	
				
					<tr>
						<td colspan="2">
							<table class="lista" width="100%">
								<caption>Discentes que realizaram adesão ao cadastro único e demonstraram interesse nesta ação de associada</caption>
									<thead>
										<td width="10%">Matrícula</td>
										<td>Discente</td>
										<td></td>
									</thead>
									<tbody>
									<c:set value="#{planoTrabalhoProjeto.inscricoesSelecao}" var="atualizar_lista" />
									<c:forEach items="#{planoTrabalhoProjeto.obj.projeto.inscricoesSelecao}" var="insc" varStatus="loop">
										<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td><h:outputText value="#{insc.discente.matricula}"/></td>
											<td><h:outputText value="#{insc.discente.nome}"/><br/>
											<b><h:outputText value="Prioritário (Segundo resolução Nº 169/2008-CONSEPE)" rendered="#{insc.prioritario}"/></b></td>
											<td>
												<h:commandLink title="Visualizar Histórico"
													action="#{ historico.selecionaDiscenteForm }" immediate="true" id="viewHistorico">
													<f:param name="id" value="#{insc.discente.id}" />
													<h:graphicImage url="/img/report.png" />
												</h:commandLink>
											</td>
										</tr>
									</c:forEach>
									</tbody>
							</table>
							<center><h:outputText rendered="#{empty planoTrabalhoProjeto.obj.projeto.inscricoesSelecao}" value="Não há discentes inscritos para esta ação associada"/></center>
						</td>
					</tr>	
									
				
			</table>
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="4">
			<h:commandButton value="Confirmar Indicação"  action="#{planoTrabalhoProjeto.indicarDiscente}" id="btfinalizar"/>
			<h:commandButton value="<< Voltar"  action="#{planoTrabalhoProjeto.iniciarIndicarDiscentePlanoLista}" id="voltar_lista"/>
			<h:commandButton value="Cancelar" action="#{planoTrabalhoProjeto.cancelar}" id="btcancelar" onclick="#{confirm }" />
		</td>
	</tr>
</tfoot>
</table>

</h:form>
</f:view>

	<script type="text/javascript">
		function dadosBancarios(id) {
			var banco = $('dados_bancarios') ;
			if (id == 4){
				banco.style.display = "";
			}else{
				banco.style.display = "none";
			}			
		}		
	</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>