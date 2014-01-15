<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.colMatricula{text-align: right !important;width: 8%;padding-right: 15px;}
</style>

<f:view>

	<a4j:keepAlive beanName="buscaDiscenteCadastroUnico" />

	<h:form>
	<h2>
		<c:if test="${empty subsistema}">
				<h:commandLink action="#{buscaDiscenteCadastroUnico.voltarAdministrativo}">Portal Administrativo</h:commandLink>
		</c:if>
		<c:if test="${not empty subsistema}">
			<ufrn:subSistema />
		</c:if>
	 &gt; Busca de Alunos no Cadastro Único
	 </h2>
	</h:form>

	<div class="descricaoOperacao">
		<p>
			Caro Professor,
		</p>	
		<br/>
		<p>
			Através desta busca é possível buscar alunos com o perfil que você deseja.
		</p>
	</div>


	<h:form id="form">
	
		<h:messages />
	
		<table class="formulario" width="100%">
			<caption>Buscar Alunos no Cadastro Único</caption>
			<tr>
				<td width="3%" nowrap="nowrap"></td>
				<td width="3%" nowrap="nowrap">Centro:</td>
				<td>
					<h:selectOneMenu value="#{buscaDiscenteCadastroUnico.parametro.centro.id}">
						<f:selectItem itemLabel=" -- SELECIONE --" itemValue="0" />
						<a4j:support reRender="cursosCombo" event="onclick" />
						<f:selectItems value="#{unidade.allCentrosEscolasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox id="checkCurso" value="#{buscaDiscenteCadastroUnico.restricao.buscaCurso}" /></td>
				<td>Curso:</td>
				<td>
					<h:selectOneMenu id="cursosCombo" value="#{buscaDiscenteCadastroUnico.parametro.curso.id}" style="width: 90%"  onclick="$('form:checkCurso').checked = true">
						<f:selectItem itemLabel=" -- SELECIONE O CENTRO --" itemValue="0" />
						<f:selectItems value="#{buscaDiscenteCadastroUnico.comboCursos}" />
					</h:selectOneMenu>
				</td>
			</tr>		
			<tr>
				<td><h:selectBooleanCheckbox id="checkArea" value="#{buscaDiscenteCadastroUnico.restricao.buscaAreaInteresse}" /></td>
				<td>Áreas de Interesse:</td>
				<td>
					<h:inputText value="#{buscaDiscenteCadastroUnico.parametro.area}" style="width: 91%" onclick="$('form:checkArea').checked = true" />
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox id="checkSemBolsa" value="#{buscaDiscenteCadastroUnico.restricao.buscaAlunosSemBolsa}" /></td>
				<td colspan="2">Alunos sem Bolsas</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" actionListener="#{buscaDiscenteCadastroUnico.buscar}" />
						<h:commandButton value="Cancelar" action="#{buscaDiscenteCadastroUnico.cancelar}" onclick="#{confirm}" immediate="true" rendered="#{not buscaDiscenteCadastroUnico.origemAdministrativo}" />
						<h:commandButton value="<< Voltar" action="#{buscaDiscenteCadastroUnico.voltarAdministrativo}" immediate="true" rendered="#{buscaDiscenteCadastroUnico.origemAdministrativo}" />
					</td>
				</tr>
			</tfoot>
			
		</table>


	<c:if test="${not empty buscaDiscenteCadastroUnico.resultado}">
		<br />
		<br />
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Detalhar Aluno
		</div>
		<br />
	
		<table class="listagem" width="90%">
			<caption>Resultado da Busca</caption>
			<thead>
				<tr>
					<th class="colMatricula">Matrícula</th>
					<th>Discente</th>
					<th align="right" width="1%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{buscaDiscenteCadastroUnico.resultado}" var="d" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td class="colMatricula">${ d.matricula }</td>
						<td>${ d.nome }</td>
						<td>
							<h:commandLink action="#{buscaDiscenteCadastroUnico.escolherDiscente}" style="border: 0;">
								<h:graphicImage url="/img/seta.gif"/>
								<f:param name="id" value="#{d.id}"/>
							</h:commandLink>						
						</td>
					</tr>
				</c:forEach>
			</tbody>
		
		</table>
	
	</c:if>

	</h:form>

</f:view>
	<c:if test="${empty subsistema}">
		<c:set var="hideSubsistema" value="true" />
	</c:if>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
