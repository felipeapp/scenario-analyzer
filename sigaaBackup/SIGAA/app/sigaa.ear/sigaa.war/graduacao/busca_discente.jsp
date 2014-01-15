<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

    <script type="text/javascript">
		JAWR.loader.style('/bundles/css/sigaa_base.css', 'all');
		JAWR.loader.style('/css/ufrn_print.css', 'print');
		
		JAWR.loader.script('/javascript/jquery/jquery.js');
    </script>
	<jwr:style src="/css/ensino/detalhes_discente.css" media="all"/>
	<jwr:script src="/javascript/graduacao/busca_discente.js"/>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:form id="formulario">

	<h2><ufrn:subSistema /> &gt; ${buscaDiscenteGraduacao.operacao.nome} &gt; Buscar Discente</h2>

	<table class="formulario" style="width:58%;">
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<!-- O nível de ensino só poderá ser selecionado dentro do módulo de diplomas e se o usuário tem permissão para registrar diploma em mais de um nível de ensino -->
			<c:if test="${buscaDiscenteGraduacao.moduloDiploma and fn:length(buscaDiscenteGraduacao.niveisHabilitados) > 1}">
				<tr>
					<td width="2%">
					</td>
					<th style="text-align: right;" width="130px" class="obrigatorio">Nível de Ensino:</th>
					<td> 
						<h:selectOneMenu value="#{buscaDiscenteGraduacao.nivelEnsinoEspecifico}" id="nivelEnsinoEspecifico">
							<f:selectItems value="#{buscaDiscenteGraduacao.niveisHabilitadosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${buscaDiscenteGraduacao.selecionaNivelEnsino}">
				<tr>
					<td width="2%">
					</td>
					<th style="text-align: right;" width="130px" class="obrigatorio">Nível de Ensino:</th>
					<td> 
						<h:selectOneMenu value="#{buscaDiscenteGraduacao.nivelEnsinoEspecifico}" id="nivelEnsinoEspecifico">
							<f:selectItems value="#{nivelEnsino.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{buscaDiscenteGraduacao.buscaMatricula}" styleClass="noborder" id="checkMatricula" />
				</td>
				<th style="text-align: left" width="130px"> 
					<label for="checkMatricula"	onclick="$('formulario:checkMatricula').checked = !$('formulario:checkMatricula').checked;">
						Matrícula:</label></th>
				<td> 
					<h:inputText value="#{buscaDiscenteGraduacao.obj.matricula}" size="14" id="matriculaDiscente" maxlength="12" title="Matrícula"
							onfocus="getEl('formulario:checkMatricula').dom.checked = true;" onkeyup="return formatarInteiro(this);" />
				</td>
			</tr>
			<c:if test="${!buscaDiscenteGraduacao.infantil}">
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDiscenteGraduacao.buscaCpf}" styleClass="noborder" id="checkCpf" /></td>
					<th style="text-align: left"> 
						<label for="checkCpf" onclick="$('formulario:checkCpf').checked = !$('formulario:checkCpf').checked;">CPF:</label></th>
					<td>
						<h:inputText value="#{buscaDiscenteGraduacao.obj.pessoa.cpf_cnpj}" size="14" id="cpfDiscente" maxlength="14" 
							onfocus="getEl('formulario:checkCpf').dom.checked = true;" onkeypress="return formataCPF(this, event, null);">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />				
					 	</h:inputText>
					</td>
				</tr>
			</c:if>	
			<tr>
				<td><h:selectBooleanCheckbox value="#{buscaDiscenteGraduacao.buscaNome}" styleClass="noborder" id="checkNome" /></td>
				<th style="text-align: left"> 
					<label for="checkNome" onclick="$('formulario:checkNome').checked = !$('formulario:checkNome').checked;">
						Nome do Discente:</label></th>
				<td><h:inputText  value="#{buscaDiscenteGraduacao.obj.pessoa.nome}" size="60" maxlength="60" id="nomeDiscente" 
							onfocus="getEl('formulario:checkNome').dom.checked = true;"/> </td>
			</tr>
			<c:choose>
				<c:when test="${!buscaDiscenteGraduacao.coordenacaoPrograma and !buscaDiscenteGraduacao.coordenacaoCurso 
						and !buscaDiscenteGraduacao.coordenacaoCursoLato and !buscaDiscenteGraduacao.infantil
						and !buscaDiscenteGraduacao.coordenacaoTecnico}">
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDiscenteGraduacao.buscaCurso}" styleClass="noborder" id="checkCurso" /></td>
					<th style="text-align: left"> 
						<label for="checkCurso" onclick="$('formulario:checkCurso').checked = !$('formulario:checkCurso').checked;">
							Curso:</label></th>
					<td><h:inputText id="curso" value="#{buscaDiscenteGraduacao.obj.curso.nome}" size="60" maxlength="60" 
								onfocus="getEl('formulario:checkCurso').dom.checked = true;" /></td>
				</tr>
				</c:when>
				<c:otherwise>
					<c:if test="${buscaDiscenteGraduacao.coordenacaoPrograma and not empty programaAtual }">
					<tr>
						<td>  </td>
						<th style="text-align: right" > <b>Programa:</b> </th>
						<td>  ${ programaAtual.nome } </td>
					</tr>
					</c:if>
					
					<c:if test="${(buscaDiscenteGraduacao.coordenacaoCurso or buscaDiscenteGraduacao.infantil or buscaDiscenteGraduacao.coordenacaoTecnico) and not empty cursoAtual }">
					<tr>
						<td>  </td>
						<th class="rotulo"> Curso: </th>
						<td> ${ buscaDiscenteGraduacao.cursoAtualCoordenacao.descricao } </td>
					</tr>
					</c:if>
					
					<c:if test="${buscaDiscenteGraduacao.coordenacaoCursoLato}">
					<tr>
						<td>  </td>
						<th class="rotulo"> Curso(s): </th>
						<td> ${ buscaDiscenteGraduacao.descricaoCursos } </td>
					</tr>
					</c:if>
				</c:otherwise>
			</c:choose>
			
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{buscaDiscenteGraduacao.buscar}" value="Buscar" id="buscar" />
					<h:commandButton action="#{buscaDiscenteGraduacao.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
	<c:if test="${fn:length(buscaDiscenteGraduacao.niveisHabilitados) > 1 or buscaDiscenteGraduacao.moduloNee}">
		<br/><%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%><br/>
	</c:if>

	</h:form>

	<c:if test="${not empty buscaDiscenteGraduacao.discentes}">
		<br />
		<center>
			<div class="infoAltRem" style="width:90%;"> 
				<c:if test="${!buscaDiscenteGraduacao.formacaoComplementar and !buscaDiscenteGraduacao.coordenacaoTecnico and !buscaDiscenteGraduacao.residencia and !buscaDiscenteGraduacao.infantil and !buscaDiscenteGraduacao.medio}">
					<h:graphicImage value="/img/comprovante.png" style="overflow: visible;" />: Visualizar Detalhes
				</c:if>
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Discente
			</div>
		</center>
		
		<h:form id="form">
		<table class="listagem" style="width:90%;">
			<caption> Selecione abaixo o discente (${fn:length(buscaDiscenteGraduacao.discentes)}) </caption>
			<thead>
				<tr>
					<th> </th>
					<th style="text-align: center;"> Matrícula </th>
					<th> </th>
					<th> Aluno </th>
					<th> Status </th>
					<th> </th>
					<th> </th>
				</tr>
			</thead>
			
				<c:set var="idFiltro" value="-1" />

				<c:forEach items="#{buscaDiscenteGraduacao.discentes}" var="discente" varStatus="status">

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
						<td colspan="7">
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
					<td ${ (discente.graduacao || discente.stricto) ? 'width="35" nowrap="nowrap"' : '' }>
						<c:if test="${discente.graduacao || discente.stricto}">
							<a href="javascript: void(0);" onclick="habilitarDetalhes(${discente.id});" title="Visualizar Detalhes">
								<img src="${ctx}/img/comprovante.png" />
								<h:graphicImage value="/img/indicator.gif" id="indicator_${discente.id}" style="display: none;" />
							</a>							
						</c:if>
					</td>				
					<td width="9%" style="text-align: center;">${discente.matricula}</td>
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
						<%-- rendered="#{! buscaDiscenteGraduacao.apenasBusca }" --%>
						<h:commandButton image="/img/seta.gif" actionListener="#{buscaDiscenteGraduacao.escolheDiscente}"
								title="Selecionar Discente" rendered="#{! buscaDiscenteGraduacao.apenasBusca }" id="selecionarDiscente">
							<f:attribute name="idDiscente" value="#{discente.id}" />
						</h:commandButton>
					</td>
					
				</tr>
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
					<td colspan="7" id="linha_${discente.id}" class="detalhesDiscente"></td>				
				</tr>
				</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="7" style="text-align: center; font-weight: bold;">
						${fn:length(buscaDiscenteGraduacao.discentes)} discente(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>