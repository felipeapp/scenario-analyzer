<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Indicar Discente do Plano de Trabalho</h2>


	<div class="descricaoOperacao">
		<b>Tipos de Vínculo</b><br/>
		<ul>
			<li>Bolsista FAEx:</b> bolsista mantido com recursos concedidos pelo FAEx.</li>
			<li>Bolsista Externo:</b> bolsista mantido com recursos de outros orgãos. CNPq, Petrobrás, Ministério da Saúde, etc.</li>
			<li>Voluntário:</b> são membros da equipe da ação de extensão que não são remunerados.</li>
			<li>Atividade Curricular:</b> são discentes não remunerados que fazem parte da equipe da ação de extensão.</li>
		<ul>				
	</div>
	
<h:form id="form">

<table class="formulario" width="90%">
<caption>Indicar Discente</caption>
<tbody>
	<tr>
		<th width="21%"> Código: </th>
		<td colspan="2"> ${planoTrabalhoExtensao.obj.atividade.codigo} </td>
	</tr>
	<tr>
		<th width="21%"> Título da Extensão: </th>
		<td colspan="2"> ${planoTrabalhoExtensao.obj.atividade.titulo} </td>
	</tr>
	<tr>
		<th> Coordenador(a): </th>
		<td colspan="2">${planoTrabalhoExtensao.obj.orientador.pessoa.nome }</td>
	</tr>

	<tr>
		<th> Local de Trabalho: </th>
		<td colspan="2"><ufrn:format type="texto" name="planoTrabalhoExtensao" property="obj.localTrabalho"/></td>
	</tr>

	<c:if test="${ not empty planoTrabalhoExtensao.obj.discenteExtensao }">
		<tr><td colspan="4" class="subFormulario" style="text-align: center"> Finalização </td></tr>
	</c:if>


	<c:if test="${not empty planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior}">
		<tr>
			<th> Discente Anterior:</th>
			<td colspan="2">
				<c:choose>
					<c:when test="${not empty planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior.discente}">
						${planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior.discente.pessoa.nome}
					</c:when>
					<c:otherwise>
						<i> Não definido </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>		
		<tr>
			<th> Motivo da Substituição:</th>
			<td colspan="2"> ${ planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior.motivoSubstituicao } </td>
		</tr>
	</c:if>


	<c:if test="${ not empty planoTrabalhoExtensao.obj.discenteExtensao }">
		<tr>
			<th> Discente Atual: </th>
			<td colspan="2">
				<c:choose>
					<c:when test="${not empty planoTrabalhoExtensao.obj.discenteExtensao}">
						${planoTrabalhoExtensao.obj.discenteExtensao.discente.pessoa.nome}
					</c:when>
					<c:otherwise>
						<i> Não definido </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th>Tipo de Vínculo: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.descricao}"/>
			</td>
		</tr>
		<tr>
			<th>Situação: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.situacaoDiscenteExtensao.descricao}"/>
			</td>
		</tr>		
		
		<tr>
			<th> <b>Data de Início:</b> </th>
			<td colspan="2">
				<fmt:formatDate value="${planoTrabalhoExtensao.obj.discenteExtensao.dataInicio}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
				
		<tr>
			<th> <b>Data da finalização:</b> </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.dataFim}" id="fimDiscente"/>
			</td>
		</tr>
		<tr>
			<th class="required"> <b>Motivo da Substituição:</b></th>
			<td>
				<select onchange="javascript: $('form:motivo').value = this.value;" id="motipoSubstituicao">
					<option value=""> -- SELECIONE O MOTIVO -- </option>
					<option value="SAÚDE"> SAÚDE </option>
					<option value="VÍNCULO EMPREGATÍCIO"> VÍNCULO EMPREGATÍCIO </option>
					<option value="MUDANÇA DE PROJETO"> MUDANÇA DE PROJETO </option>
					<option value="CONCLUSÃO DA GRADUAÇÃO"> CONCLUSÃO DA GRADUAÇÃO </option>
					<option value="A PEDIDO DO ALUNO"> A PEDIDO DO ALUNO </option>
					<option value="FALECIMENTO"> FALECIMENTO </option>
					<option value="OUTROS"> OUTROS </option>
				</select>
				&nbsp;
				<h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.motivoSubstituicao}" size="40" id="motivo"/>
			</td>
		</tr>
		
	</c:if>
	
	<tr>
		<td colspan="4" class="subFormulario" style="text-align: center"> Indicação </td>
	</tr>

	<tr>
		<td colspan="4">
				
			<table style="subFormulario" width="100%">
				<tr>
					<th width="20%" class="required"><b>Discente:</b></th>
					<td>
						<h:inputHidden id="idDiscente" value="#{ planoTrabalhoExtensao.obj.discenteExtensaoNovo.discente.id }"/>
						<h:inputText id="nomeDiscente" value="#{ planoTrabalhoExtensao.obj.discenteExtensaoNovo.discente.pessoa.nome }" style="width: 95%"/>
	
						<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..." id="imgIndicadorDiscente"/> </span>
						
					</td>
				</tr>
				
				<tr>
					<th  class="required"><b>Tipo de Vículo:</b></th>
					<td>
						<h:selectOneMenu id="tipoVinculo"
							value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.tipoVinculo.id}"	style="width: 40%;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM VÍNCULO --"/>
							<f:selectItems value="#{tipoVinculoDiscenteBean.allAtivosExtensaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th> <b>Data de Início:</b> </th>
					<td colspan="2">
						<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.dataInicio}" id="inicioDiscente"/>
					</td>
				</tr>
				
					<tr>
						<td colspan="3">
							<div class="descricaoOperacao">
							Para os bolsistas remunerados, informe abaixo seus dados bancários.
							Observação: a conta informada não pode ser Conta Conjunta ou Conta Poupança.
							</div>
						</td>
					</tr>
					
					<tr>
						<th class="required"> Banco:</th>
						<td>
							<h:selectOneMenu value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.banco.id}" id="bancoDiscenteExtensao">
								<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM BANCO --"  />
								<f:selectItems value="#{discenteMonitoria.allBancoCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>					
					<tr>
						<th class="required"> Nº Agência: </th>
						<td> <h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.agencia}" size="10" maxlength="15" id="numAgenciaDiscenteExtensao"/>	</td>
					</tr>
					<tr>
						<th class="required"> Nº Conta Corrente: </th>
						<td> <h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.conta}" size="10" maxlength="15" id="numeroContaCorrente"/>	</td>
					</tr>
					<tr>
						<th> Operação: </th>
						<td> <h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.operacao}" size="4" maxlength="15" id="numeroOperacao"/>	</td>
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
								<caption>Discentes que realizaram adesão ao cadastro único e demonstraram interesse nesta ação de extensão</caption>
									<thead>
										<tr>
											<td width="10%">Matrícula</td>
											<td>Discente</td>
											<td></td>
										</tr>	
									</thead>
									<tbody>
									<c:set value="#{planoTrabalhoExtensao.inscricoesSelecao}" var="atualizar_lista" />
									<c:forEach items="#{planoTrabalhoExtensao.obj.atividade.inscricoesSelecao}" var="insc" varStatus="loop">
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
							<center><h:outputText rendered="#{empty planoTrabalhoExtensao.obj.atividade.inscricoesSelecao}" value="Não há discentes inscritos para esta ação"/></center>
						</td>
					</tr>	
					
					
					
				
			</table>
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="4">
			<h:commandButton value="Confirmar Indicação"  action="#{planoTrabalhoExtensao.indicarDiscente}" id="btfinalizar"/>
			<h:commandButton value="Cancelar" action="#{planoTrabalhoExtensao.cancelar}" id="btcancelar" onclick="#{confirm }" />
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