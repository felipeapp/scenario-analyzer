<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Indicar Discente do Plano de Trabalho</h2>


	<div class="descricaoOperacao">
		<b>Tipos de V�nculo</b><br/>
		<ul>
			<li>Bolsista FAEx:</b> bolsista mantido com recursos concedidos pelo FAEx.</li>
			<li>Bolsista Externo:</b> bolsista mantido com recursos de outros org�os. CNPq, Petrobr�s, Minist�rio da Sa�de, etc.</li>
			<li>Volunt�rio:</b> s�o membros da equipe da a��o de extens�o que n�o s�o remunerados.</li>
			<li>Atividade Curricular:</b> s�o discentes n�o remunerados que fazem parte da equipe da a��o de extens�o.</li>
		<ul>				
	</div>
	
<h:form id="form">

<table class="formulario" width="90%">
<caption>Indicar Discente</caption>
<tbody>
	<tr>
		<th width="21%"> C�digo: </th>
		<td colspan="2"> ${planoTrabalhoExtensao.obj.atividade.codigo} </td>
	</tr>
	<tr>
		<th width="21%"> T�tulo da Extens�o: </th>
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
		<tr><td colspan="4" class="subFormulario" style="text-align: center"> Finaliza��o </td></tr>
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
						<i> N�o definido </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>		
		<tr>
			<th> Motivo da Substitui��o:</th>
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
						<i> N�o definido </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th>Tipo de V�nculo: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.descricao}"/>
			</td>
		</tr>
		<tr>
			<th>Situa��o: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.situacaoDiscenteExtensao.descricao}"/>
			</td>
		</tr>		
		
		<tr>
			<th> <b>Data de In�cio:</b> </th>
			<td colspan="2">
				<fmt:formatDate value="${planoTrabalhoExtensao.obj.discenteExtensao.dataInicio}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
				
		<tr>
			<th> <b>Data da finaliza��o:</b> </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.dataFim}" id="fimDiscente"/>
			</td>
		</tr>
		<tr>
			<th class="required"> <b>Motivo da Substitui��o:</b></th>
			<td>
				<select onchange="javascript: $('form:motivo').value = this.value;" id="motipoSubstituicao">
					<option value=""> -- SELECIONE O MOTIVO -- </option>
					<option value="SA�DE"> SA�DE </option>
					<option value="V�NCULO EMPREGAT�CIO"> V�NCULO EMPREGAT�CIO </option>
					<option value="MUDAN�A DE PROJETO"> MUDAN�A DE PROJETO </option>
					<option value="CONCLUS�O DA GRADUA��O"> CONCLUS�O DA GRADUA��O </option>
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
		<td colspan="4" class="subFormulario" style="text-align: center"> Indica��o </td>
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
					<th  class="required"><b>Tipo de V�culo:</b></th>
					<td>
						<h:selectOneMenu id="tipoVinculo"
							value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.tipoVinculo.id}"	style="width: 40%;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM V�NCULO --"/>
							<f:selectItems value="#{tipoVinculoDiscenteBean.allAtivosExtensaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th> <b>Data de In�cio:</b> </th>
					<td colspan="2">
						<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.dataInicio}" id="inicioDiscente"/>
					</td>
				</tr>
				
					<tr>
						<td colspan="3">
							<div class="descricaoOperacao">
							Para os bolsistas remunerados, informe abaixo seus dados banc�rios.
							Observa��o: a conta informada n�o pode ser Conta Conjunta ou Conta Poupan�a.
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
						<th class="required"> N� Ag�ncia: </th>
						<td> <h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.agencia}" size="10" maxlength="15" id="numAgenciaDiscenteExtensao"/>	</td>
					</tr>
					<tr>
						<th class="required"> N� Conta Corrente: </th>
						<td> <h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.conta}" size="10" maxlength="15" id="numeroContaCorrente"/>	</td>
					</tr>
					<tr>
						<th> Opera��o: </th>
						<td> <h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensaoNovo.operacao}" size="4" maxlength="15" id="numeroOperacao"/>	</td>
					</tr>
					
					<tr>
						<td class="subFormulario" colspan="2">
							<div class="infoAltRem">
								<html:img page="/img/report.png" style="overflow: visible;"/>: Hist�rico
							</div>
						</td>
					</tr>	
				
					<tr>
						<td colspan="2">
							<table class="lista" width="100%">
								<caption>Discentes que realizaram ades�o ao cadastro �nico e demonstraram interesse nesta a��o de extens�o</caption>
									<thead>
										<tr>
											<td width="10%">Matr�cula</td>
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
											<b><h:outputText value="Priorit�rio (Segundo resolu��o N� 169/2008-CONSEPE)" rendered="#{insc.prioritario}"/></b></td>
											<td>
												<h:commandLink title="Visualizar Hist�rico"
													action="#{ historico.selecionaDiscenteForm }" immediate="true" id="viewHistorico">
													<f:param name="id" value="#{insc.discente.id}" />
													<h:graphicImage url="/img/report.png" />
												</h:commandLink>
											</td>
										</tr>
									</c:forEach>
									</tbody>
							</table>
							<center><h:outputText rendered="#{empty planoTrabalhoExtensao.obj.atividade.inscricoesSelecao}" value="N�o h� discentes inscritos para esta a��o"/></center>
						</td>
					</tr>	
					
					
					
				
			</table>
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="4">
			<h:commandButton value="Confirmar Indica��o"  action="#{planoTrabalhoExtensao.indicarDiscente}" id="btfinalizar"/>
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