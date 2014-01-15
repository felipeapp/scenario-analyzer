<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoOuvidoria" />

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>
	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Buscar Discente</h2>
		
		<div id="ajuda" class="descricaoOperacao">
			<p>Para prosseguir com o cadastro, favor buscar e selecionar o discente desejado utilizando o formulário abaixo.</p>
		</div>
		
		<table class="formulario" style="width:58%;">
			<caption> Informe os critérios de busca</caption>
			<tbody>
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{manifestacaoOuvidoria.buscaMatricula}" styleClass="noborder" id="checkMatricula" />
					</td>
					<th style="text-align: left" width="130px"> 
						<label for="checkMatricula"	onclick="$('formulario:checkMatricula').checked = !$('formulario:checkMatricula').checked;">
							Matrícula:</label></th>
					<td> 
						<h:inputText value="#{manifestacaoOuvidoria.matricula}" size="14" id="matriculaDiscente" maxlength="12"
								onfocus="getEl('formulario:checkMatricula').dom.checked = true;" onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{manifestacaoOuvidoria.buscaNome}" styleClass="noborder" id="checkNome" /></td>
					<th style="text-align: left"> 
						<label for="checkNome" onclick="$('formulario:checkNome').checked = !$('formulario:checkNome').checked;">
							Nome do Discente:</label></th>
					<td><h:inputText  value="#{manifestacaoOuvidoria.nome}" size="60" maxlength="60" id="nomeDiscente" 
								onfocus="getEl('formulario:checkNome').dom.checked = true;"/> </td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{manifestacaoOuvidoria.buscaCurso}" styleClass="noborder" id="checkCurso" /></td>
					<th style="text-align: left"> 
						<label for="checkCurso" onclick="$('formulario:checkCurso').checked = !$('formulario:checkCurso').checked;">
							Curso:</label></th>
					<td><h:inputText id="curso" value="#{manifestacaoOuvidoria.curso}" size="60" maxlength="60" 
								onfocus="getEl('formulario:checkCurso').dom.checked = true;" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{manifestacaoOuvidoria.buscarDiscente}" value="Buscar" id="buscar" />
						<h:commandButton id="btn_anterior" value="<< Passo Anterior" action="#{manifestacaoOuvidoria.paginaTipoSolicitante }" />
						<h:commandButton action="#{manifestacaoOuvidoria.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br /><br />
	
	<h:form id="form">
		<c:if test="${manifestacaoOuvidoria.discentes != null && not empty manifestacaoOuvidoria.discentes }">
		<center>
			<div class="infoAltRem" style="width:90%;"> 
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Discente
			</div>
		</center>
		<br />
		
		<table class="listagem" style="width:90%;">
			<caption> Selecione abaixo o discente (${fn:length(manifestacaoOuvidoria.discentes)}) </caption>
			<thead>
				<tr>
					<th style="text-align: right; padding-right: 1%;" width="8%"> Matrícula </th>
					<th> </th>
					<th> Aluno </th>
					<th> Status </th>
					<th> </th>
				</tr>
			</thead>
			
			<c:set var="idFiltro" value="-1" />

			<c:forEach items="#{manifestacaoOuvidoria.discentes}" var="discente" varStatus="status">

				<c:set var="idLoop" value="${discente.curso.id}" />
				
				<c:if test="${not empty cursoAtual and discente.graduacao}">
					<c:set var="idLoop" value="${discente.matrizCurricular.id}" />
					<c:if test="${discente.EAD}">
						<c:set var="idLoop" value="${discente.polo.id}" />
					</c:if>
				</c:if>
				
				<c:if test="${ idFiltro != idLoop}">
				
					<c:set var="idFiltro" value="${discente.curso.id}" />
					
					<c:if test="${not empty cursoAtual and discente.graduacao}">
					
						<c:set var="idFiltro" value="${discente.matrizCurricular.id}" />
					
						<c:if test="${discente.EAD}">
							<c:set var="idFiltro" value="${discente.polo.id}" />
						</c:if>
					</c:if>
				
					<c:if test="${discente.curso.id eq 0 and empty cursoAtual and discente.stricto and discente.discente.emAssociacao}">
					
						<c:set var="idFiltro" value="${discente.id}" />
					
					</c:if>
				
										
					<tr class="curso">
						<td colspan="5">
							<c:if test="${discente.curso.id gt 0 and empty cursoAtual}">
								${discente.curso.descricao}
							</c:if>
							<c:if test="${discente.curso.id eq 0 and empty cursoAtual and !discente.discente.emAssociacao}">
								${(discente.graduacao or discente.stricto) ? 'ALUNO ESPECIAL' : 'SEM CURSO'}
							</c:if>
							<c:if test="${discente.curso.id eq 0 and empty cursoAtual and discente.stricto and discente.discente.emAssociacao}">
								${(discente.stricto) ? 'ALUNO EM ASSOCIACAO (PROGRAMA EM REDE)' : 'SEM CURSO'}
							</c:if>
							<c:if test="${not empty cursoAtual and discente.graduacao and !discente.EAD}">
								${discente.matrizCurricular.descricaoMin }
							</c:if>
							<c:if test="${not empty cursoAtual and discente.graduacao and discente.EAD}">
								${discente.polo.cidade.nome}
							</c:if>
							<c:if test="${discente.infantil}">
								${discente.curso.descricao}
							</c:if>
						</td>
					</tr>
				</c:if>

				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td width="9%" style="text-align: right; padding-right: 1%;">${discente.matricula}</td>
					<td width="10">
						<c:if test="${discente.idFoto != null}">
						</c:if>
					</td>
					<td>${discente.nome}
						<c:if test="${ discente.stricto && !discente.regular && !discente.emAssociacao }">
							(${ discente.nivelDesc }${ discente.gestoraAcademica == null ? '' : ' - ' }${ discente.gestoraAcademica == null 
									? '' : discente.gestoraAcademica.nome })
						</c:if>
						<c:if test="${ discente.stricto && discente.emAssociacao }">
							(${ discente.nivelDesc }${ discente.gestoraAcademica == null ? '' : ' - ' }${ discente.gestoraAcademica == null 
									? '' : discente.gestoraAcademica.nome })
						</c:if>
					</td>
					<td width="8%">${discente.statusString}</td>
					<td align="right" width="2%">
						<h:commandButton image="/img/seta.gif" title="Selecionar Discente" actionListener="#{manifestacaoOuvidoria.selecionarPessoa }" styleClass="noborder">
							<f:attribute name="idSelecionado" value="#{discente.id}" />
						</h:commandButton>
					</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
					<td colspan="5" id="linha_${discente.id}" class="detalhesDiscente"></td>				
				</tr>
				</c:forEach>
			
				<tfoot>
					<tr>
						<td colspan="5" style="text-align: center; font-weight: bold;">
							${fn:length(manifestacaoOuvidoria.discentes)} discente(s) encontrado(s)
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>