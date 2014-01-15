<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Indentificação do Docente</b></caption>
		<tr>
			<th>Nome:</th>
			<td colspan="5"><b><h:outputText
				value="#{relatorioProdocente.obj.docente.pessoa.nome }" /></b></td>
		</tr>
		<tr>
			<th>Matrícula:</th>
			<td><b><h:outputText value="#{relatorioProdocente.obj.docente.siape }" /></b></td>
			<th>CPF:</th>
			<td colspan="3"><b><h:outputText
				value="#{relatorioProdocente.obj.docente.pessoa.cpf_cnpj }" /></b></td>
		</tr>
		<tr>
			<th>Cargo:</th>
			<td></td>
			<th>Nível:</th>
			<td></td>
			<th>Regime de Trabalho:</th>
			<td></td>
		</tr>
		<tr>
			<th>Centro:</th>
			<td colspan="5"><b><h:outputText
				value="#{relatorioProdocente.obj.docente.unidade.unidadeResponsavel.nome}" /></b></td>
		</tr>
		<tr>
			<th>Departamento:</th>
			<td colspan="5"><b><h:outputText
				value="#{relatorioProdocente.obj.docente.unidade.nome}" /></b></td>
		</tr>
	</table>
	<hr>
	<table>
		<!-- grupos dos Relatórios -->
		<tr>
			<td>
			<h3>1. Cargos de Direção e funcoes Administrativas Gratificadas</h3>
			</td>
		</tr>
		<tr>
			<!-- 1.Cargos de Direção e funcoes Administrativas -->
			<td><t:dataTable align="center" width="100%"
				styleClass="listagem" rowClasses="linhaPar,linhaImpar"
				value="#{relatorioProdocente.obj.cargoDirecaoFuncaoGratificada}" var="item">
				<t:column>
					<f:facet name="header">
						<h:outputText value="Cargo ou Função" />
					</f:facet>
					<h:outputText value="#{item.designacaoCargo.descricao}" />
				</t:column>
				<t:column>
					<f:facet name="header">
						<h:outputText value="Período" />
					</f:facet>
					<h:outputText value="#{item.dataInicio}" />
					<f:verbatim> - </f:verbatim>
					<h:outputText value="#{item.dataFim}" />
				</t:column>
				<t:column>
					<f:facet name="header">
						<h:outputText value="Meses" />
					</f:facet>
					<h:outputText value="0" />
				</t:column>
				<t:column>
					<f:facet name="header">
						<h:outputText value="Total Pontos" />
					</f:facet>
					<h:outputText value="0" />
				</t:column>

			</t:dataTable></td>
		</tr>
		<tr>
			<td>
			<h3>2. Atividades de Ensino</h3>
			</td>
		</tr>
		<tr>
			<!-- 2.1 - Atividades de Ensino Graduação -->
			<td>
			<p><c:if
				test="${not empty relatorioProdocente.obj.atividadeEnsinoGraduacao}">
				<c:forEach items="${relatorioProdocente.obj.atividadeEnsinoGraduacao}"
					var="item">
					<c:if test="${grupo ne item.itemAvaliacaoDocente.id }">
						<c:if test="${grupo ne null }">
	</table>
	</c:if>
	<c:set var="grupo" value="${item.itemAvaliacaoDocente.id}"
		scope="request" />
	<br>
	<span class="header"><b>${item.itemAvaliacaoDocente.numeroTopico/100}
	- ${item.itemAvaliacaoDocente.descricao}</b></span>
	<br>
	<TABLE style="width:100%" cellpadding=2 cellspacing=0>
		<tr bgcolor="#CECECE">
			<td WIDTH="5%">&nbsp;</td>
			<td WIDTH="60%">Disciplina</td>
			<td WIDTH="15%">Período</td>
			<td WIDTH="10%">CH</td>
			<td WIDTH="10%">Remunerado</td>
			<td WIDTH="10%">Total Pontos</td>
		</tr>

		</c:if>
		<tr>
			<td></td>
			<td>${item.nomeDisciplina}</td>
			<td>${item.semestre}</td>
			<td>${item.cargaHoraria}</td>
			<td align="center">${item.remunerado ? "Sim" : "Não"}</td>
			<td>${item.totalPontos}</td>
		</tr>
		</c:forEach>
	</TABLE>
	</c:if>
	</p>
	</td>

	</tr>
	<tr>
		<!-- 2.2 - Atividades de Ensino -->
		<td>
		<p><c:if
			test="${not empty relatorioProdocente.obj.atividadeEnsinoPosGraduacao}">
			<c:forEach items="${relatorioProdocente.obj.atividadeEnsinoPosGraduacao}"
				var="item">
				<c:if test="${grupo ne item.itemAvaliacaoDocente.id }">
					<c:if test="${grupo ne null }">
						</table>
					</c:if>
					<c:set var="grupo" value="${item.itemAvaliacaoDocente.id}"
						scope="request" />
					<br>
					<span class="header"><b>${item.itemAvaliacaoDocente.numeroTopico/100}
					- ${item.itemAvaliacaoDocente.descricao}</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td WIDTH="5%">&nbsp;</td>
							<td WIDTH="60%">Disciplina</td>
							<td WIDTH="15%">Período</td>
							<td WIDTH="10%">CH</td>
							<td WIDTH="10%">Remunerado</td>
							<td WIDTH="10%">Total Pontos</td>
						</tr>

						</c:if>
						<tr>
							<td></td>
							<td>${item.nomeDisciplina}</td>
							<td>${item.semestre}</td>
							<td>${item.cargaHoraria}</td>
							<td align="center">${item.remunerado ? "Sim" : "Não"}</td>
							<td>${item.totalPontos}</td>
						</tr>
						</c:forEach>
					</TABLE>
				</c:if></p>
		</td>

	</tr>
	<tr>
		<!-- 2.3 - Orientação - Estágio Supervisionado e correlatos -->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesGraduacaoEstagioDocente}">
					<br> <span class="header"><b>2.03 - Orientação - Estágio Supervisionado e correlatos</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td WIDTH="60%">Nome do Projeto</td>
							<td WIDTH="5%">Tipo</td>
							<td WIDTH="30%">Período</td>
							<td WIDTH="5%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesGraduacaoEstagioDocente}"	var="item">
						<tr>
							<td>${item.nomeProjeto}</td>
							<td>ES</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.4 - Orientação - Trabalho ou Projeto Final de Curso concluído -->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesGraduacaoTrabalhoFimCursoDocente1 or not empty relatorioProdocente.obj.orientacoesGraduacaoTrabalhoFimCursoDocente2}">
					<br> <span class="header"><b>2.04 - Orientação - Trabalho ou Projeto Final de Curso concluído</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td WIDTH="60%">Nome do Projeto</td>
							<td WIDTH="5%">Tipo</td>
							<td WIDTH="30%">Período</td>
							<td WIDTH="5%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesGraduacaoTrabalhoFimCursoDocente1}" var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>TFC</td>
							<td>${item.dataDefesa}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.orientacoesGraduacaoTrabalhoFimCursoDocente2}" var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>TFC</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.5 - Orientação - Especialização-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosEspecializacaoDocente}">
					<br> <span class="header"><b>2.05 - Orientação -  Especialização</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Período</td>
						    <td WIDTH="10%">Defesa</td>
						    <td  WIDTH="5%">Meses</td>
						    <td  WIDTH="5%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesPosEspecializacaoDocente}"	var="item">
						<tr>
							<td>${item.orientando}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>${item.dataPublicacao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.6 - Orientação - Orientação de Mestrado na UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosMestradoDocente}">
					<br> <span class="header"><b>2.06 - Orientação - Mestrado na ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Período</td>
						    <td WIDTH="10%">Defesa</td>
						    <td  WIDTH="5%">Meses</td>
						    <td  WIDTH="5%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesPosMestradoDocente}"	var="item">
						<tr>
							<td>${item.orientando}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>${item.dataPublicacao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.7 - Orientação - Orientação de Doutorado UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosDoutoradoDocente}">
					<br> <span class="header"><b>2.05 - Orientação - Doutorado ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Período</td>
						    <td WIDTH="10%">Defesa</td>
						    <td  WIDTH="5%">Meses</td>
						    <td  WIDTH="5%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesPosDoutoradoDocente}"	var="item">
						<tr>
							<td>${item.orientando}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>${item.dataPublicacao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.8 - Orientação - Orientação de Tese ou dissertação de Mestrado concluída na UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosMestradoConcluidoDocente}">
					<br> <span class="header"><b>2.08 - Orientação de Tese ou dissertação de Mestrado concluída na ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Defesa</td>
						    <td  WIDTH="5%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesPosMestradoConcluidoDocente}"	var="item">
						<tr>
							<td>${item.orientando}</td>
							<td>${item.dataPublicacao}</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.9 - Orientação - Orientação de Tese de Doutorado concluída na UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosDoutoradoConcluidoDocente}">
					<br> <span class="header"><b>2.08 - Orientação de Tese de Doutorado concluída na ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Defesa</td>
						    <td  WIDTH="5%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.orientacoesPosDoutoradoConcluidoDocente}"	var="item">
						<tr>
							<td>${item.orientando}</td>
							<td>${item.dataPublicacao}</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 3.Producão Intelectual -->
		<td>
		<h3>3. Produção Intelectual</h3>
		<c:forEach items="${relatorioProdocente.obj.chavesProducaoIntelectual}" var="chave">
			<c:forEach items="${relatorioProdocente.obj.producoesIntelectuais[chave]}" var="item">
				<c:if test="${grupo ne null }">
						</table>
					</c:if>
					<c:set var="grupo" value="${item.itemAvaliacaoDocente.id}"
						scope="request" />
					<br>
					<span class="header"><b>${item.itemAvaliacaoDocente.numeroTopico/100}
					- ${item.itemAvaliacaoDocente.descricao}</b></span>
					<br>
					<TABLE WIDTH="90%" BORDER=0 cellpadding=0 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td>&nbsp;</td>
							<td>Atividade</td>
							<td>Período</td>
							<td>Total Pontos</td>
							<td>Pontos GED</td>
						</tr>

						</c:if>
						<tr>
							<td colspan="2">${item.nomeAtividade}</td>
							<td><ufrn:format name="item" property="periodoInicio"
								type="data" /> - <ufrn:format name="item" property="periodoFim"
								type="data" /></td>
							<td>${item.totalPontos}</td>
							<td>${item.totalGed}</td>

						</tr>
			</c:forEach>
					</table>
					<br>
		</c:forEach>






   	<%--<h3>3. Produção Intelectual</h3>
		<p><c:if test="${not empty relatorioProdocente.obj.producoesIntelectuais}">
			<c:forEach items="${relatorioProdocente.obj.producoesIntelectuais}" var="item">
				<c:if test="${grupo ne item.itemAvaliacaoDocente.id }">
					<c:if test="${grupo ne null }">
						</table>
					</c:if>
					<c:set var="grupo" value="${item.itemAvaliacaoDocente.id}"
						scope="request" />
					<br>
					<span class="header"><b>${item.itemAvaliacaoDocente.numeroTopico/100}
					- ${item.itemAvaliacaoDocente.descricao}</b></span>
					<br>
					<TABLE WIDTH="90%" BORDER=0 cellpadding=0 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td>&nbsp;</td>
							<td>Atividade</td>
							<td>Período</td>
							<td>Total Pontos</td>
							<td>Pontos GED</td>
						</tr>

						</c:if>
						<tr>
							<td colspan="2">${item.nomeAtividade}</td>
							<td><ufrn:format name="item" property="periodoInicio"
								type="data" /> - <ufrn:format name="item" property="periodoFim"
								type="data" /></td>
							<td>${item.totalPontos}</td>
							<td>${item.totalGed}</td>

						</tr>
						</c:forEach>
					</table>
					<br>
				</c:if></p>--%>
		</td>
	</tr>
	<tr>
		<td>
		<h3>4.Atividade de Pesquisa e Extensão</h3>
		<tr>
		<!-- 4.1 - Relatório parcial ou final de atividades (Internacional) de Extensão-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeInternacional1 or not empty relatorioProdocente.obj.relatorioAtividadeInternacional2}">
					<br> <span class="header"><b>4.01 - Relatório parcial ou final de atividades (Internacional) de Extensão</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publicação</td>
						    <td ALIGN=CENTER WIDTH="10%">Participação</td>
						    <td ALIGN=CENTER WIDTH="10%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeInternacional1}" var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataAprovacao}</td>
							<td>${item.tipoParticipacao.descricao}</td>

							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeInternacional2}" var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>

							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.2 - Relatório parcial ou final de atividades (Nacional) de Extensão-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeNacional1 or not empty relatorioProdocente.obj.relatorioAtividadeNacional2}">
					<br> <span class="header"><b>4.02 - Relatório, parcial ou final, de atividades (nacional) de extensão, aprovado em instâncias competentes na ${ configSistema['siglaInstituicao'] }</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publicação</td>
						    <td ALIGN=CENTER WIDTH="10%">Participação</td>
						    <td ALIGN=CENTER WIDTH="10%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeNacional1}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataAprovacao}</td>
							<td>${item.tipoParticipacao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeNacional2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.3 - Relatório parcial ou final de atividades (Regional ou Local) de Extensão-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeRegionalLocal1 or not empty relatorioProdocente.obj.relatorioAtividadeRegionalLocal2}">
					<br> <span class="header"><b>4.03 - Relatório, parcial ou final, de atividades (regional ou local) de extensão, aprovado em instâncias competentes na ${ configSistema['siglaInstituicao'] } </b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publicação</td>
						    <td ALIGN=CENTER WIDTH="10%">Participação</td>
						    <td ALIGN=CENTER WIDTH="10%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeRegionalLocal1}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataAprovacao}</td>
							<td>${item.tipoParticipacao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeRegionalLocal2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.4 - Atividade em cursos de extensão, devidamente comprovadas por instância responsável pela emissão dos certificados, aprovados em instâncias competentes na UFRN e Cadastrados na PROEX-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeCurso1 or not empty relatorioProdocente.obj.relatorioAtividadeCurso2}">
					<br> <span class="header"><b>4.04 - Atividade em cursos de extensão, devidamente comprovadas por instância responsável pela emissão dos certificados, aprovados em instâncias competentes na ${configSistema['siglaInstituicao']} e Cadastrados na PROEX</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publicação</td>
						    <td ALIGN=CENTER WIDTH="10%">Participação</td>
						    <td ALIGN=CENTER WIDTH="10%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeCurso1}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataAprovacao}</td>
							<td>${item.tipoParticipacao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeCurso2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.5 - Atividades de assessoria, mini-curso em congresso, consultoria, perícia ou sindicância, (manutenção de obra artística) devidamente comprovadas por instância responsável pela contratação do serviço-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeAssessoria1 or not empty relatorioProdocente.obj.relatorioAtividadeAssessoria2}">
					<br> <span class="header"><b>4.05 - Atividade em cursos de extensão, devidamente comprovadas por instância responsável pela emissão dos certificados, aprovados em instâncias competentes na ${ configSistema['siglaInstituicao'] } e Cadastrados na PROEX</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publicação</td>
						    <td ALIGN=CENTER WIDTH="10%">Participação</td>
						    <td ALIGN=CENTER WIDTH="10%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeAssessoria1}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataAprovacao}</td>
							<td>${item.tipoParticipacao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeAssessoria2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.6 - Atividade de atendimento de pacientes em Hospitais ou Ambulatórios Universitários, preferencialmente com a presença de alunos. Esta atividade deverá ser devidamente cadastrada, na PROEX-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeAtendimento1 or not empty relatorioProdocente.obj.relatorioAtividadeAtendimento2}">
					<br> <span class="header"><b>4.06 - Atividade de atendimento de pacientes em Hospitais ou Ambulatórios Universitários, preferencialmente com a presença de alunos. Esta atividade deverá ser devidamente cadastrada, na PROEX</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publicação</td>
						    <td ALIGN=CENTER WIDTH="10%">Participação</td>
						    <td ALIGN=CENTER WIDTH="10%">Remunerado</td>
						    <td WIDTH="10%">Total Pontos</td>
						    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeAtendimento1}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataAprovacao}</td>
							<td>${item.tipoParticipacao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeAtendimento2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "Não"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.7 - Mini-cursos em eventos científicos, culturais e desportivos,  comprovados por certificados e aprovados em instância competentes na UFRN-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioMiniCursoEvento}">
					<br> <span class="header"><b>4.07 - Mini-cursos em eventos científicos, culturais e desportivos,  comprovados por certificados e aprovados em instância competentes na ${ configSistema['siglaInstituicao'] }</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="10%">Período</td>
							    <td WIDTH="10%">Carga Horária</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioMiniCursoEvento}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.dataInicio} - ${item.dataFim}</td>
							<td>${item.tipoChHoraria}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.8 - Patente ou produto registrado (aparelho ou instrumento, equipamento, fármaco, outros) registrado (na área de atividade acadêmica do docente)-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioPatenteProduto}">
					<br> <span class="header"><b>4.08 - Patente ou produto registrado (aparelho ou instrumento, equipamento, fármaco, outros) registrado (na área de atividade acadêmica do docente)</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioPatenteProduto}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.registroData}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.9 - Obra artística, cultural ou técnica-científica, premiada ou reconhecida em instância competente em nível internacional, na área de atividade acadêmica do docente-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioObraArtisticaInternacional}">
					<br> <span class="header"><b>4.09 - Obra artística, cultural ou técnica-científica, premiada ou reconhecida em instância competente em nível internacional, na área de atividade acadêmica do docente</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioObraArtisticaInternacional}"	var="item">
						<tr>
							<td>${item.premio}</td>
							<td>${item.data}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.10 - Obra artística, cultural ou técnica-científica, premiada ou reconhecida em instância competente em nível nacional, na área de atividade acadêmica do docente-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioObraArtisticaNacional}">
					<br> <span class="header"><b>4.10 - Obra artística, cultural ou técnica-científica, premiada ou reconhecida em instância competente em nível nacional, na área de atividade acadêmica do docente</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioObraArtisticaNacional}"	var="item">
						<tr>
							<td>${item.premio}</td>
							<td>${item.data}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.11 - Obra artística, cultural ou técnica-científica, premiada ou reconhecida em instância competente em nível regional ou local, na área de atividade acadêmica do docente-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioObraArtisticaRegionalLocal}">
					<br> <span class="header"><b>4.11 - Obra artística, cultural ou técnica-científica, premiada ou reconhecida em instância competente em nível regional ou local, na área de atividade acadêmica do docente</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioObraArtisticaRegionalLocal}"	var="item">
						<tr>
							<td>${item.premio}</td>
							<td>${item.data}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.12 - Participação em eventos científicos, desportivos ou artístico-culturais internacionais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalCoordenador}">
					<br> <span class="header"><b>4.12 - Participação em eventos científicos, desportivos ou artístico-culturais internacionais como coordenador geral</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalCoordenador}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.13 - Participação em eventos científicos, desportivos ou artístico-culturais nacionais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoNacionalCoordenador}">
					<br> <span class="header"><b>4.13 - Participação em eventos científicos, desportivos ou artístico-culturais nacionais como coordenador geral</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoNacionalCoordenador}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.14 - Participação em eventos científicos, desportivos ou artístico-culturais regionais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoRegionalCoordenador}">
					<br> <span class="header"><b>4.14 - Participação em eventos científicos, desportivos ou artístico-culturais regionais como coordenador geral</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoRegionalCoordenador}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.15 - Participação em eventos científicos, desportivos ou artístico-culturais locais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoLocalCoordenador}">
					<br> <span class="header"><b>4.15 - Participação em eventos científicos, desportivos ou artístico-culturais locais como coordenador geral</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoLocalCoordenador}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.16 -Participação em eventos científicos, desportivos ou artístico-culturais internacionais na Comissão Organizadora-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalMembro}">
					<br> <span class="header"><b>4.16 - Participação em eventos científicos, desportivos ou artístico-culturais internacionais na Comissão Organizadora</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalMembro}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.17 - Participação em eventos científicos, desportivos ou artístico-culturais nacionais na Comissão Organizadora-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoNacionalMembro}">
					<br> <span class="header"><b>4.17 - Participação em eventos científicos, desportivos ou artístico-culturais internacionais na Comissão Organizadora</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoNacionalMembro}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.18 - Participação em eventos científicos, desportivos ou artístico-culturais regionais ou locais, na Comissão Organizadora-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoRegionalMembro}">
					<br> <span class="header"><b>4.18 - Participação em eventos científicos, desportivos ou artístico-culturais regionais ou locais, na Comissão Organizadora</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoRegionalMembro}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.19 - Participação em visita ou missão Internacional, devidamente autorizada pela instituição para desenvolver atividades acadêmicas-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoVisitaMissaoInternacional}">
					<br> <span class="header"><b>4.19 - Participação em visita ou missão Internacional, devidamente autorizada pela instituição para desenvolver atividades acadêmicas</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoVisitaMissaoInternacional}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.20 - Participação em eventos científicos, desportivos ou artístico-culturais internacionais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalConvidado}">
					<br> <span class="header"><b>4.20 - Participação em eventos científicos, desportivos ou artístico-culturais internacionais como Conferencista Convidado</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalConvidado}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.21 - Participação em eventos científicos, desportivos ou artístico-culturais nacionais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoNacionalConvidado}">
					<br> <span class="header"><b>4.21 - Participação em eventos científicos, desportivos ou artístico-culturais nacionais como Conferencista Convidado</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoNacionalConvidado}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.22 - Participação em eventos científicos, desportivos ou artístico-culturais regionais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoRegionalConvidado}">
					<br> <span class="header"><b>4.22 - Participação em eventos científicos, desportivos ou artístico-culturais regionais como Conferencista Convidado</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoRegionalConvidado}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.23 - Participação em eventos científicos, desportivos ou artístico-culturais locais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoLocalConvidado}">
					<br> <span class="header"><b>4.23 - Participação em eventos científicos, desportivos ou artístico-culturais locais como Conferencista Convidado</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="15%">Data</td>
							    <td WIDTH="10%">Total Pontos</td>
							    <td WIDTH="10%">Pontos GED</td>
						</tr>
					<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoEventoLocalConvidado}"	var="item">
						<tr>
							<td>${item.veiculo}</td>
							<td>${item.periodoInicio} - ${item.periodoFim}</td>
							<td>0</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<!-- FIM DO ITEM 4 -->
	<tr>
		<td>
		<h3>5.Atividades de Qualificação</h3>
		<table>
			<tr>
				<!-- 5.1 - Curso de Mestrado, Doutorado ou estágio Pós-Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioCursoMestradoDoutoradoEstagioDocente}">
							<br> <span class="header"><b>5.01 - Curso de Mestrado, Doutorado ou estágio Pós-Doutorado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="20%">Nível do Curso</td>
									    <td WIDTH="60%">Período do Afastamento</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioCursoMestradoDoutoradoEstagioDocente}"	var="item">
								<tr>
									<td>${item.tipoQualificacao.descricao}</td>
									<td>${item.dataInicial} - ${item.dataFinal}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 5.2 - Avaliação do desempenho pelo Orientador-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioAvaliacaoDesempenhoOrientador}">
							<br> <span class="header"><b>5.02 - Avaliação do desempenho pelo Orientador</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="20%">Nível do Curso</td>
									    <td WIDTH="60%">Data do Parecer</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioAvaliacaoDesempenhoOrientador}"	var="item">
								<tr>
									<td>${item.tipoQualificacao.descricao} (Parecer Favorável)</td>
									<td>${item.dataFinal}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 5.3 - Título de Especialista obtido no período avaliado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioEspecialistaDocente}">
							<br> <span class="header"><b>5.02 - Título de Especialista obtido no período avaliado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="60%">Atividade</td>
    									 <td WIDTH="15%">Data</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioEspecialistaDocente}"	var="item">
								<tr>
									<td>${item.titulo} (Parecer Favorável)</td>
									<td>${item.dataInicio} - ${item.dataFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<!--FIM  5.Atividades de Qualificação -->
		<td></td>
	</tr>
	<tr>
		<td>
		<h3>6.Atividades Administrativas e de Representação</h3>
		<table>
			<tr>
				<!-- 6.1 - Vice-chefe de departamento acadêmico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioViceChefeDepto}">
							<br> <span class="header"><b>6.01 - Vice-chefe de departamento acadêmico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="60%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioViceChefeDepto}"	var="item">
								<tr>
									<td>${item.designacaoCargo.descricao}</td>
									<td>${item.dataInicio} - ${item.dataFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.2 - Vice-coordenador de curso de graduação ou Pós-Graduação-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioViceCoordenadorCurso}">
							<br> <span class="header"><b>6.02 - Vice-coordenador de curso de graduação ou Pós-Graduação</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="60%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioViceCoordenadorCurso}"	var="item">
								<tr>
									<td>${item.designacaoCargo.descricao}</td>
									<td>${item.dataInicio} - ${item.dataFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.3 - Participação em Colegiados (excluir os membros natos) - só contará o colegiado para o qual o conselheiro foi indicado por eleição (não vale aquele por delegação do cargo ou representação)-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoColegiado}">
							<br> <span class="header"><b>6.03 - Participação em Colegiados (excluir os membros natos) - só contará o colegiado para o qual o conselheiro foi indicado por eleição</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Reuniões</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoColegiado}"	var="item">
								<tr>
									<td>${item.comissao}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.4 - Participação em comissão de criação de novos cursos e reformulação de projeto pedagógico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoNovosCursos}">
							<br> <span class="header"><b>6.04 - Participação em comissão de criação de novos cursos e reformulação de projeto pedagógico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Reuniões</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoComissaoNovosCursos}"	var="item">
								<tr>
									<td>${item.comissao}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.5 - Participação em comissão permanente de progressão horizontal e vertical, designado por portaria-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoPermanente}">
							<br> <span class="header"><b>6.05 - Participação em comissão permanente de progressão horizontal e vertical, designado por portaria</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Reuniões</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoComissaoPermanente}"	var="item">
								<tr>
									<td>${item.comissao}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.6 - Participação em comissões temporárias-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoTemporarias}">
							<br> <span class="header"><b>6.06 - Participação em comissões temporárias</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Reuniões</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoComissaoTemporarias}"	var="item">
								<tr>
									<td>${item.comissao}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.7 - Participação em comissões de sindicância ou de processos de natureza disciplinar-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoSindicancia}">
							<br> <span class="header"><b>6.07 - Participação em comissões de sindicância ou de processos de natureza disciplinar</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Período</td>
    									 <td WIDTH="10%">Total Pontos</td>
    									 <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioParticipacaoComissaoSindicancia}"	var="item">
								<tr>
									<td>${item.comissao}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.8 - Chefia ou coordenação de setores acadêmicos de apoio: laboratórios, núcleos de estudos, bibliotecas, oficinas ou órgão similar, descrito pela PRH como tipo 3 e designado por portaria-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioChefiaCoordenacaoSetoresAcademicoApoio}">
							<br> <span class="header"><b>6.08 - Chefia ou coordenação de setores acadêmicos de apoio: laboratórios, núcleos de estudos, bibliotecas, oficinas ou órgão similar, descrito pela PRH como tipo 3 e designado por portaria</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="80%">Atividade</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioChefiaCoordenacaoSetoresAcademicoApoio}"	var="item">
								<tr>
									<td>${item.tipoChefia.descricao}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.9 - Coordenação de Base de Pesquisa-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioChefiaCoordenacaoBasePesquisa}">
							<br> <span class="header"><b>6.09 - Coordenação de Base de Pesquisa</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="80%">Atividade</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioChefiaCoordenacaoBasePesquisa}"	var="item">
								<tr>
									<td>${item.tipoChefia.descricao}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.10 - Coordenação de curso de pós-graduação lato sensu (devidamente comprovado que não recebe remuneração para esta função)-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioChefiaCoordenacaoCursoLato}">
							<br> <span class="header"><b>6.10 - Coordenação de curso de pós-graduação lato sensu (devidamente comprovado que não recebe remuneração para esta função)</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="55%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Remunerado</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioChefiaCoordenacaoCursoLato}"	var="item">
								<tr>
									<td>${item.chefia.nome}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td align="center">${item.pago ? "Sim" : "Não"}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.11 - Consultor "ad hoc" de revista internacional-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocRevistaInternacional}">
							<br> <span class="header"><b>6.11 - Consultor "ad hoc" de revista internacional</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocRevistaInternacional}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.12 - Consultor "ad hoc" de revista nacional-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocRevistaNacional}">
							<br> <span class="header"><b>6.12 - Consultor "ad hoc" de revista nacional</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocRevistaNacional}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.13 - Consultor "ad hoc" de revista regional ou local-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocRevistaRegionalLocal}">
							<br> <span class="header"><b>6.13 - Consultor "ad hoc" de revista regional ou local</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocRevistaRegionalLocal}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.14 - Consultor "ad hoc" de anais de evento internacional-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocAnaisInternacional}">
							<br> <span class="header"><b>6.14 - Consultor "ad hoc" de anais de evento internacional</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocAnaisInternacional}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.15 - Consultor "ad hoc" de anais de evento nacional-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocAnaisNacionalRegional}">
							<br> <span class="header"><b>6.15 - Consultor "ad hoc" de anais de evento nacional</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocAnaisNacionalRegional}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.16 - Consultor "ad hoc" de órgão de fomento-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocOrgaoFormento}">
							<br> <span class="header"><b>6.16 - Consultor "ad hoc" de órgão de fomento</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocOrgaoFormento}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.17 - Representação acadêmica e participação em órgãos de formulação e execução de políticas públicas de ensino, ciência e tecnologia-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioRepresentacaoAcademica}">
							<br> <span class="header"><b>6.17 - Representação acadêmica e participação em órgãos de formulação e execução de políticas públicas de ensino, ciência e tecnologia</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioRepresentacaoAcademica}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 6.18 - Consultor "ad hoc" de comissão nacional de reforma e avaliação de cursos-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocReformaAvaliacaoCurso}">
							<br> <span class="header"><b>6.18 - Consultor "ad hoc" de comissão nacional de reforma e avaliação de cursos</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Período</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioConsultorAdHocReformaAvaliacaoCurso}"	var="item">
								<tr>
									<td>${item.veiculo}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<!-- 6.Atividades Administrativas e de Representação -->
		<td></td>
	</tr>
	<tr>
		<td>
		<h3>7.Outras Atividades</h3>
		<table>
			<tr>
				<!-- 7.1 - Participação em banca examinadora de concurso público para professor Titular ou Livre Docência-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorTitular}">
							<br> <span class="header"><b>7.01 - Participação em banca examinadora de concurso público para professor Titular ou Livre Docência</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaConcursoProfessorTitular}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.2 - Participação em banca examinadora de concurso público para professor Adjunto, Assistente ou Auxiliar-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorAdjAssAux}">
							<br> <span class="header"><b>7.02 - Participação em banca examinadora de concurso público para professor Adjunto, Assistente ou Auxiliar</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaConcursoProfessorAdjAssAux}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.3 - Participação em banca examinadora de concurso público para professor Substituto-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorSubstituto}">
							<br> <span class="header"><b>7.03 - Participação em banca examinadora de concurso público para professor Substituto</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaConcursoProfessorSubstituto}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.4 - Participação em banca examinadora de concurso público para professor Nível Médio-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorMedio}">
							<br> <span class="header"><b>7.04 - Participação em banca examinadora de concurso público para professor Nível Médio</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaConcursoProfessorMedio}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.5 - Participação em banca examinadora de concurso público para Técnico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoTecnico}">
							<br> <span class="header"><b>7.05 - Participação em banca examinadora de concurso público para Técnico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaConcursoTecnico}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.6 - Participação em banca examinadora de Tese de Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaTeseDoutorado}">
							<br> <span class="header"><b>7.06 - Participação em banca examinadora de Tese de Doutorado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaTeseDoutorado}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.7 - Participação em banca examinadora de Tese ou Dissertação de Mestrado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaTeseMestrado}">
							<br> <span class="header"><b>7.07 - Participação em banca examinadora de Tese ou Dissertação de Mestrado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaTeseMestrado}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.8 - Participação em banca examinadora de Qualificação de Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaQualificacaoDoutorado}">
							<br> <span class="header"><b>7.08 - Participação em banca examinadora de Qualificação de Doutorado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaQualificacaoDoutorado}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.9 - Participação em banca examinadora de Qualificação de Mestrado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaQualificacaoMestrado}">
							<br> <span class="header"><b>7.09 - Participação em banca examinadora de Qualificação de Mestrado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaQualificacaoMestrado}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.10 - Participação em banca examinadora de Monografia de Especialização-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaMonografiaEspecializacao}">
							<br> <span class="header"><b>7.10 - Participação em banca examinadora de Monografia de Especialização</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaMonografiaEspecializacao}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.11 - Participação em banca examinadora de Seleção de Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaSelecaoDoutorado}">
							<br> <span class="header"><b>7.11 - Participação em banca examinadora de Seleção de Doutorado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaSelecaoDoutorado}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.12 - Participação em banca examinadora de Seleção de Mestrado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaSelecaoMestrado}">
							<br> <span class="header"><b>7.12 - Participação em banca examinadora de Seleção de Mestrado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaSelecaoMestrado}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.13 - Participação em Banca Examinadora de Monografia de Graduação-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaMonografiaGraduacao}">
							<br> <span class="header"><b>7.13 - Participação em Banca Examinadora de Monografia de Graduação</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaMonografiaGraduacao}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.14 - Participação em banca examinadora de Seleção de Especialização-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaSelecaoEspecializacao}">
							<br> <span class="header"><b>7.14 - Participação em banca examinadora de Seleção de Especialização</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="60%">Atividade</td>
										    <td WIDTH="15%">Data</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioBancaSelecaoEspecializacao}"	var="item">
								<tr>
									<td>${item.titulo}</td>
									<td>${item.data}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.15 - Disciplina cursada (com aprovação) como parte de programa de pós-graduação Lato Sensu sem afastamento-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioDisciplinaCursadaLato}">
							<br> <span class="header"><b>7.15 - Disciplina cursada (com aprovação) como parte de programa de pós-graduação Lato Sensu sem afastamento</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="65%">Nível do Curso</td>
										    <td WIDTH="15%">Período</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioDisciplinaCursadaLato}"	var="item">
								<tr>
									<td>${item.disciplina}</td>
									<td>${item.qualificacaoDocente.dataFinal}</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.16 - Orientador Acadêmico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioOrientadorAcademico}">
							<br> <span class="header"><b>7.16 - Orientador Acadêmico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="50%">Atividade</td>
										    <td WIDTH="15%">Período</td>
										    <td WIDTH="10%">Reuniões</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioOrientadorAcademico}" var="item">
								<tr>
									<td>${item.comissao}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
			<tr>
				<!-- 7.17 - Orientação de alunos de graduação: Iniciação científica, PET, Iniciação Tecnológica, Extensão, Monitoria e Apoio Técnico em atividades acadêmicas-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioOrientacaoAlunosGraduacao1 or not empty relatorioProdocente.obj.relatorioOrientacaoAlunosGraduacao2}">
							<br> <span class="header"><b>7.17 - Orientação de alunos de graduação: Iniciação científica, PET, Iniciação Tecnológica, Extensão, Monitoria e Apoio Técnico em atividades acadêmicas</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="50%">Atividade</td>
										    <td WIDTH="15%">Período</td>
										    <td WIDTH="10%">Reuniões</td>
										    <td WIDTH="10%">Total Pontos</td>
										    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioOrientacaoAlunosGraduacao1}" var="item">
								<tr>
									<td>${item.orientando}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							<c:forEach items="${relatorioProdocente.obj.relatorioOrientacaoAlunosGraduacao2}" var="item">
								<tr>
									<td>${item.nomeAluno}</td>
									<td>${item.dataInicio} - ${item.dataFinal}</td>
									<td>1</td>
									<td>0</td>
									<td>0</td>
								</tr>
							</c:forEach>
							</TABLE>
					</c:if>
				</p>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<!-- 7.Outras Atividades -->
		<td></td>
	</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
