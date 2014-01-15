<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
	<h2> <ufrn:subSistema /> > Reposição de Avaliação > Confirmação da Apreciação</h2>
	<c:set var="confirmHomologacao" value="return true;" scope="application"/>	
	<c:if test="${solicitacaoReposicaoProva.parecerPendente}">
		<p style="text-align: center; color: red;">
			<b>A T E N Ç Ã O!</b><br/>
			Existe(m) Solicitação(ões) com o Parecer do Docente Pendente ou Indeferido pelo mesmo.<br/>
		</p>
		<c:set var="confirmHomologacao" value="return confirm('Tem Certeza que Deseja Continuar mesmo com Parecer Pendente ou Indeferido?');" scope="application"/>			
		<br/>
	</c:if>
	
<h:form id="form">	
	<table class="visualizacao" width="80%">
		<caption>Dados da Apreciação</caption>
		<tr>
			<th>Situação:</th>
			<td>${solicitacaoReposicaoProva.obj.descricaoStatus}</td>
		</tr>
		<c:if test="${not empty solicitacaoReposicaoProva.observacao}">
			<tr>
				<th>Observação:</th>
				<td>
					${ solicitacaoReposicaoProva.observacao }
				</td>
			</tr>						
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">Alunos Selecionados:</td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="listagem">
					<thead>
						<tr>
							<td>Data da Solicitação</td>
							<td>Discente</td>
							<td>Curso</td>
							<td style="text-align: center;">Data da Avaliação</td>
							<td>Situação</td>
						</tr>
					</thead>
					
					<c:set var="aval" value="0"/>
					<c:forEach items="#{solicitacaoReposicaoProva.listaConfirmados}" var="solicitacoes" varStatus="loop">
						<c:if test="${aval != solicitacoes.dataAvaliacao.id}">
							<tr>
								<td colspan="6" style="background-color: #C8D5EC">
									<c:set var="aval" value="#{solicitacoes.dataAvaliacao.id}"/>
									<b>${solicitacoes.turma.descricaoCodigo} - 
									Avaliação: ${solicitacoes.dataAvaliacao.descricao}</b>
								</td>
							</tr>		
						</c:if>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td><ufrn:format type="dataHora" valor="${solicitacoes.dataCadastro}"/></td>
							<td>
								<h:outputText value="#{solicitacoes.discente.matriculaNome}"/>
							</td>
							<td>
								<h:outputText value="#{solicitacoes.discente.curso.descricao}"/>
							</td>
							<td style="text-align: center;"><ufrn:format type="data" valor="${solicitacoes.dataProvaSugerida }"/></td>
							<td>
								<c:if test="${empty solicitacoes.statusParecer}">
									<span style="color: red; font-weight: bold;">Parecer Pendente</span>
								</c:if> 
								<c:if test="${not empty solicitacoes.statusParecer}">
									${solicitacoes.statusParecer.descricao}
								</c:if> 							
							</td>					
						</tr>									
					</c:forEach>
				</table>			
			</td>		
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{solicitacaoReposicaoProva.cadastrarHomologacao}" onclick="#{confirmHomologacao}"/>
					<h:commandButton value="< Voltar" action="#{solicitacaoReposicaoProva.registrarHomologacao}"/>
					<h:commandButton value="Cancelar" action="#{solicitacaoReposicaoProva.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>			
		</tfoot>
	</table>			
<br/>
	<c:set var="exibirApenasSenha" value="true" scope="request"/>
	<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>