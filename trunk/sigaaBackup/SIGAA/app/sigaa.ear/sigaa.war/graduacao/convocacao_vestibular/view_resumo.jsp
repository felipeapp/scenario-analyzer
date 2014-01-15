<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="resumoConvocacaoVestibularMBean"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Resumo de Convocação de Candidatos </h2>

<h:form>

<table class="visualizacao">
	<caption>Resumo da Convocação</caption>
	<tr>
		<th width="50%">Processo Seletivo:</th>
		<td><h:outputText id="psVest" value="#{resumoConvocacaoVestibularMBean.obj.processoSeletivo.nome}"/></td>
	</tr>
	<tr>
		<th>Descrição:</th>
		<td><h:outputText id="descricao" value="#{resumoConvocacaoVestibularMBean.obj.descricao}"/></td>
	</tr>
	<tr>
		<th>Data da Convocação:</th>
		<td>
			<h:outputText id="data" value="#{resumoConvocacaoVestibularMBean.obj.dataConvocacao}" >
				<f:converter converterId="convertData" />
			</h:outputText>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Convocações (${fn:length(resumoConvocacaoVestibularMBean.convocacoes)})</caption>
				<thead>
					<tr>
						<th style="text-align: left; width: 5%;">Ordem</th>
						<th style="text-align: left; width: 5%;">Nº Inscrição</th>
						<th style="text-align: center; width: 12%;">CPF</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;">Classificação</th>
						<th style="text-align: left; width: 10%;">Ingresso</th>
						<th style="text-align: left; width: 15%;">Tipo de Convocação</th>
						<th style="text-align: left; width: 5%;">Dentro das Vagas</th>
						<th style="text-align: left; width: 7%;">Cota</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty resumoConvocacaoVestibularMBean.convocacoes}">
						<c:set var="_matriz_loop" />
						<c:set var="_ordem" value="1"/>
					
						<c:forEach items="#{resumoConvocacaoVestibularMBean.convocacoes}" var="conv" varStatus="loop">
							<c:if test="${_matriz_loop != conv.discente.matrizCurricular.id}">
								<c:set var="_matriz_loop" value="${conv.discente.matrizCurricular.id}"/>
								<c:set var="_ordem" value="1"/>
								<tr>
									<td class="subFormulario" colspan="9"> 
										<h:outputText value="#{conv.discente.matrizCurricular.descricao}"/> 
									</td>
								</tr>
							</c:if>
						
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>
									${_ordem}
									<c:set var="_ordem" value="${_ordem + 1}"/>
								</td>
								<td><h:outputText value="#{conv.inscricaoVestibular.numeroInscricao}" /></td>
								<td style="text-align: center;">
									<h:outputText value="#{conv.discente.pessoa.cpf_cnpj}" >
										<f:converter converterId="convertCpf"/>
									</h:outputText>
								</td>
								<td><h:outputText value="#{conv.discente.pessoa.nome}"/></td>
								<td><h:outputText value="#{conv.resultado.classificacao > 0 ? conv.resultado.classificacao : 'AMA'}"/></td>
								<td><h:outputText value="#{conv.discente.periodoIngresso}"/>º semestre</td>
								<td><h:outputText value="#{conv.tipoDesc}"/></td>
								<td style="text-align: left;"><ufrn:format type="simNao" valor="${conv.dentroNumeroVagas}"/></td>
								<td style="text-align: left;">
									<h:outputText value="#{conv.grupoCotaConvocado.descricao}"/>
									<h:outputText value="*" rendered="#{ conv.grupoCotaRemanejado }" title="Convocado para preenchimento de vaga remanescente no grupo de cotas"/>
								</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="9" style="text-align: center; color: red;">Nenhum candidato foi convocado.</td>
					</c:otherwise>
				</c:choose>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Cancelamentos (${fn:length(resumoConvocacaoVestibularMBean.cancelamentos)})</caption>
				<thead>
					<tr>
						<th style="text-align: center; width: 15%;">Matrícula</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left; ">Motivo do Cancelamento</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty resumoConvocacaoVestibularMBean.cancelamentos}">
						<c:set var="_matriz_loop" />
						
						<c:forEach items="#{resumoConvocacaoVestibularMBean.cancelamentos}" var="c" varStatus="loop">
							<c:if test="${_matriz_loop != c.convocacao.discente.matrizCurricular.id}">
								<c:set var="_matriz_loop" value="${c.convocacao.discente.matrizCurricular.id}"/>
								<tr>
									<td class="subFormulario" colspan="3"> <h:outputText value="#{c.convocacao.discente.matrizCurricular.descricao}"/> </td>
								</tr>
							</c:if>
							
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align: center;"><h:outputText value="#{c.convocacao.discente.matricula}" /></td>
								<td><h:outputText value="#{c.convocacao.discente.pessoa.nome}"/></td>
								<td><h:outputText value="#{c.motivo.descricao}"/></td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="3" style="text-align: center; color: red;">Nenhum cancelamento foi realizado.</td>
					</c:otherwise>
				</c:choose>
			</table>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center;">
				<h:commandButton value="<< Voltar" action="#{ resumoConvocacaoVestibularMBean.telaFormulario }" id="btnVoltar"/>
				<h:commandButton value="Cancelar" action="#{ resumoConvocacaoVestibularMBean.cancelar }" id="btnCancelar"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>