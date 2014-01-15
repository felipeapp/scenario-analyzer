<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Indentifica��o do Docente</b></caption>
		<tr>
			<th>Nome:</th>
			<td colspan="5"><b><h:outputText
				value="#{relatorioProdocente.obj.docente.pessoa.nome }" /></b></td>
		</tr>
		<tr>
			<th>Matr�cula:</th>
			<td><b><h:outputText value="#{relatorioProdocente.obj.docente.siape }" /></b></td>
			<th>CPF:</th>
			<td colspan="3"><b><h:outputText
				value="#{relatorioProdocente.obj.docente.pessoa.cpf_cnpj }" /></b></td>
		</tr>
		<tr>
			<th>Cargo:</th>
			<td></td>
			<th>N�vel:</th>
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
		<!-- grupos dos Relat�rios -->
		<tr>
			<td>
			<h3>1. Cargos de Dire��o e funcoes Administrativas Gratificadas</h3>
			</td>
		</tr>
		<tr>
			<!-- 1.Cargos de Dire��o e funcoes Administrativas -->
			<td><t:dataTable align="center" width="100%"
				styleClass="listagem" rowClasses="linhaPar,linhaImpar"
				value="#{relatorioProdocente.obj.cargoDirecaoFuncaoGratificada}" var="item">
				<t:column>
					<f:facet name="header">
						<h:outputText value="Cargo ou Fun��o" />
					</f:facet>
					<h:outputText value="#{item.designacaoCargo.descricao}" />
				</t:column>
				<t:column>
					<f:facet name="header">
						<h:outputText value="Per�odo" />
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
			<!-- 2.1 - Atividades de Ensino Gradua��o -->
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
			<td WIDTH="15%">Per�odo</td>
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
			<td align="center">${item.remunerado ? "Sim" : "N�o"}</td>
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
							<td WIDTH="15%">Per�odo</td>
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
							<td align="center">${item.remunerado ? "Sim" : "N�o"}</td>
							<td>${item.totalPontos}</td>
						</tr>
						</c:forEach>
					</TABLE>
				</c:if></p>
		</td>

	</tr>
	<tr>
		<!-- 2.3 - Orienta��o - Est�gio Supervisionado e correlatos -->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesGraduacaoEstagioDocente}">
					<br> <span class="header"><b>2.03 - Orienta��o - Est�gio Supervisionado e correlatos</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td WIDTH="60%">Nome do Projeto</td>
							<td WIDTH="5%">Tipo</td>
							<td WIDTH="30%">Per�odo</td>
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
		<!-- 2.4 - Orienta��o - Trabalho ou Projeto Final de Curso conclu�do -->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesGraduacaoTrabalhoFimCursoDocente1 or not empty relatorioProdocente.obj.orientacoesGraduacaoTrabalhoFimCursoDocente2}">
					<br> <span class="header"><b>2.04 - Orienta��o - Trabalho ou Projeto Final de Curso conclu�do</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
							<td WIDTH="60%">Nome do Projeto</td>
							<td WIDTH="5%">Tipo</td>
							<td WIDTH="30%">Per�odo</td>
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
		<!-- 2.5 - Orienta��o - Especializa��o-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosEspecializacaoDocente}">
					<br> <span class="header"><b>2.05 - Orienta��o -  Especializa��o</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Per�odo</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.6 - Orienta��o - Orienta��o de Mestrado na UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosMestradoDocente}">
					<br> <span class="header"><b>2.06 - Orienta��o - Mestrado na ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Per�odo</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.7 - Orienta��o - Orienta��o de Doutorado UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosDoutoradoDocente}">
					<br> <span class="header"><b>2.05 - Orienta��o - Doutorado ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Nome do Orientando</td>
						    <td WIDTH="10%">Per�odo</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.8 - Orienta��o - Orienta��o de Tese ou disserta��o de Mestrado conclu�da na UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosMestradoConcluidoDocente}">
					<br> <span class="header"><b>2.08 - Orienta��o de Tese ou disserta��o de Mestrado conclu�da na ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 2.9 - Orienta��o - Orienta��o de Tese de Doutorado conclu�da na UFRN e outras IFES-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.orientacoesPosDoutoradoConcluidoDocente}">
					<br> <span class="header"><b>2.08 - Orienta��o de Tese de Doutorado conclu�da na ${ configSistema['siglaInstituicao'] } e outras IFES</b></span>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 3.Produc�o Intelectual -->
		<td>
		<h3>3. Produ��o Intelectual</h3>
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
							<td>Per�odo</td>
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






   	<%--<h3>3. Produ��o Intelectual</h3>
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
							<td>Per�odo</td>
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
		<h3>4.Atividade de Pesquisa e Extens�o</h3>
		<tr>
		<!-- 4.1 - Relat�rio parcial ou final de atividades (Internacional) de Extens�o-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeInternacional1 or not empty relatorioProdocente.obj.relatorioAtividadeInternacional2}">
					<br> <span class="header"><b>4.01 - Relat�rio parcial ou final de atividades (Internacional) de Extens�o</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publica��o</td>
						    <td ALIGN=CENTER WIDTH="10%">Participa��o</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeInternacional2}" var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>

							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.2 - Relat�rio parcial ou final de atividades (Nacional) de Extens�o-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeNacional1 or not empty relatorioProdocente.obj.relatorioAtividadeNacional2}">
					<br> <span class="header"><b>4.02 - Relat�rio, parcial ou final, de atividades (nacional) de extens�o, aprovado em inst�ncias competentes na ${ configSistema['siglaInstituicao'] }</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publica��o</td>
						    <td ALIGN=CENTER WIDTH="10%">Participa��o</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeNacional2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.3 - Relat�rio parcial ou final de atividades (Regional ou Local) de Extens�o-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeRegionalLocal1 or not empty relatorioProdocente.obj.relatorioAtividadeRegionalLocal2}">
					<br> <span class="header"><b>4.03 - Relat�rio, parcial ou final, de atividades (regional ou local) de extens�o, aprovado em inst�ncias competentes na ${ configSistema['siglaInstituicao'] } </b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publica��o</td>
						    <td ALIGN=CENTER WIDTH="10%">Participa��o</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeRegionalLocal2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.4 - Atividade em cursos de extens�o, devidamente comprovadas por inst�ncia respons�vel pela emiss�o dos certificados, aprovados em inst�ncias competentes na UFRN e Cadastrados na PROEX-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeCurso1 or not empty relatorioProdocente.obj.relatorioAtividadeCurso2}">
					<br> <span class="header"><b>4.04 - Atividade em cursos de extens�o, devidamente comprovadas por inst�ncia respons�vel pela emiss�o dos certificados, aprovados em inst�ncias competentes na ${configSistema['siglaInstituicao']} e Cadastrados na PROEX</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publica��o</td>
						    <td ALIGN=CENTER WIDTH="10%">Participa��o</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeCurso2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.5 - Atividades de assessoria, mini-curso em congresso, consultoria, per�cia ou sindic�ncia, (manuten��o de obra art�stica) devidamente comprovadas por inst�ncia respons�vel pela contrata��o do servi�o-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeAssessoria1 or not empty relatorioProdocente.obj.relatorioAtividadeAssessoria2}">
					<br> <span class="header"><b>4.05 - Atividade em cursos de extens�o, devidamente comprovadas por inst�ncia respons�vel pela emiss�o dos certificados, aprovados em inst�ncias competentes na ${ configSistema['siglaInstituicao'] } e Cadastrados na PROEX</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publica��o</td>
						    <td ALIGN=CENTER WIDTH="10%">Participa��o</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeAssessoria2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.6 - Atividade de atendimento de pacientes em Hospitais ou Ambulat�rios Universit�rios, preferencialmente com a presen�a de alunos. Esta atividade dever� ser devidamente cadastrada, na PROEX-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioAtividadeAtendimento1 or not empty relatorioProdocente.obj.relatorioAtividadeAtendimento2}">
					<br> <span class="header"><b>4.06 - Atividade de atendimento de pacientes em Hospitais ou Ambulat�rios Universit�rios, preferencialmente com a presen�a de alunos. Esta atividade dever� ser devidamente cadastrada, na PROEX</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	<td WIDTH="50%">Atividade</td>
						    <td WIDTH="10%">Data Publica��o</td>
						    <td ALIGN=CENTER WIDTH="10%">Participa��o</td>
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
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					<c:forEach items="${relatorioProdocente.obj.relatorioAtividadeAtendimento2}"	var="item">
						<tr>
							<td>${item.titulo}</td>
							<td>${item.inicioReal} - ${item.terminoReal}</td>
							<td>${item.tipoAtividadeExtensao.descricao}</td>
							<td>0</td>
							<td align="center">${item.pago ? "Sim" : "N�o"}</td>
							<td>0</td>
						</tr>
					</c:forEach>
					</TABLE>
			</c:if>
		</p>
		</td>
	</tr>
	<tr>
		<!-- 4.7 - Mini-cursos em eventos cient�ficos, culturais e desportivos,  comprovados por certificados e aprovados em inst�ncia competentes na UFRN-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioMiniCursoEvento}">
					<br> <span class="header"><b>4.07 - Mini-cursos em eventos cient�ficos, culturais e desportivos,  comprovados por certificados e aprovados em inst�ncia competentes na ${ configSistema['siglaInstituicao'] }</b></span>
					<br>
					<TABLE style="width:100%" cellpadding=2 cellspacing=0>
						<tr bgcolor="#CECECE">
 						 	 	<td WIDTH="65%">Atividade</td>
							    <td WIDTH="10%">Per�odo</td>
							    <td WIDTH="10%">Carga Hor�ria</td>
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
		<!-- 4.8 - Patente ou produto registrado (aparelho ou instrumento, equipamento, f�rmaco, outros) registrado (na �rea de atividade acad�mica do docente)-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioPatenteProduto}">
					<br> <span class="header"><b>4.08 - Patente ou produto registrado (aparelho ou instrumento, equipamento, f�rmaco, outros) registrado (na �rea de atividade acad�mica do docente)</b></span>
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
		<!-- 4.9 - Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel internacional, na �rea de atividade acad�mica do docente-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioObraArtisticaInternacional}">
					<br> <span class="header"><b>4.09 - Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel internacional, na �rea de atividade acad�mica do docente</b></span>
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
		<!-- 4.10 - Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel nacional, na �rea de atividade acad�mica do docente-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioObraArtisticaNacional}">
					<br> <span class="header"><b>4.10 - Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel nacional, na �rea de atividade acad�mica do docente</b></span>
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
		<!-- 4.11 - Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel regional ou local, na �rea de atividade acad�mica do docente-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioObraArtisticaRegionalLocal}">
					<br> <span class="header"><b>4.11 - Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel regional ou local, na �rea de atividade acad�mica do docente</b></span>
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
		<!-- 4.12 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalCoordenador}">
					<br> <span class="header"><b>4.12 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais como coordenador geral</b></span>
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
		<!-- 4.13 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais nacionais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoNacionalCoordenador}">
					<br> <span class="header"><b>4.13 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais nacionais como coordenador geral</b></span>
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
		<!-- 4.14 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais regionais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoRegionalCoordenador}">
					<br> <span class="header"><b>4.14 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais regionais como coordenador geral</b></span>
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
		<!-- 4.15 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais locais como coordenador geral-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoLocalCoordenador}">
					<br> <span class="header"><b>4.15 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais locais como coordenador geral</b></span>
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
		<!-- 4.16 -Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais na Comiss�o Organizadora-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalMembro}">
					<br> <span class="header"><b>4.16 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais na Comiss�o Organizadora</b></span>
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
		<!-- 4.17 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais nacionais na Comiss�o Organizadora-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoNacionalMembro}">
					<br> <span class="header"><b>4.17 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais na Comiss�o Organizadora</b></span>
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
		<!-- 4.18 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais regionais ou locais, na Comiss�o Organizadora-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoRegionalMembro}">
					<br> <span class="header"><b>4.18 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais regionais ou locais, na Comiss�o Organizadora</b></span>
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
		<!-- 4.19 - Participa��o em visita ou miss�o Internacional, devidamente autorizada pela institui��o para desenvolver atividades acad�micas-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoVisitaMissaoInternacional}">
					<br> <span class="header"><b>4.19 - Participa��o em visita ou miss�o Internacional, devidamente autorizada pela institui��o para desenvolver atividades acad�micas</b></span>
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
		<!-- 4.20 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoInternacionalConvidado}">
					<br> <span class="header"><b>4.20 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais internacionais como Conferencista Convidado</b></span>
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
		<!-- 4.21 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais nacionais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoNacionalConvidado}">
					<br> <span class="header"><b>4.21 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais nacionais como Conferencista Convidado</b></span>
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
		<!-- 4.22 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais regionais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoRegionalConvidado}">
					<br> <span class="header"><b>4.22 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais regionais como Conferencista Convidado</b></span>
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
		<!-- 4.23 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais locais como Conferencista Convidado-->
		<td>
		<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoEventoLocalConvidado}">
					<br> <span class="header"><b>4.23 - Participa��o em eventos cient�ficos, desportivos ou art�stico-culturais locais como Conferencista Convidado</b></span>
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
		<h3>5.Atividades de Qualifica��o</h3>
		<table>
			<tr>
				<!-- 5.1 - Curso de Mestrado, Doutorado ou est�gio P�s-Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioCursoMestradoDoutoradoEstagioDocente}">
							<br> <span class="header"><b>5.01 - Curso de Mestrado, Doutorado ou est�gio P�s-Doutorado</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="20%">N�vel do Curso</td>
									    <td WIDTH="60%">Per�odo do Afastamento</td>
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
				<!-- 5.2 - Avalia��o do desempenho pelo Orientador-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioAvaliacaoDesempenhoOrientador}">
							<br> <span class="header"><b>5.02 - Avalia��o do desempenho pelo Orientador</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="20%">N�vel do Curso</td>
									    <td WIDTH="60%">Data do Parecer</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioAvaliacaoDesempenhoOrientador}"	var="item">
								<tr>
									<td>${item.tipoQualificacao.descricao} (Parecer Favor�vel)</td>
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
				<!-- 5.3 - T�tulo de Especialista obtido no per�odo avaliado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioEspecialistaDocente}">
							<br> <span class="header"><b>5.02 - T�tulo de Especialista obtido no per�odo avaliado</b></span>
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
									<td>${item.titulo} (Parecer Favor�vel)</td>
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
		<!--FIM  5.Atividades de Qualifica��o -->
		<td></td>
	</tr>
	<tr>
		<td>
		<h3>6.Atividades Administrativas e de Representa��o</h3>
		<table>
			<tr>
				<!-- 6.1 - Vice-chefe de departamento acad�mico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioViceChefeDepto}">
							<br> <span class="header"><b>6.01 - Vice-chefe de departamento acad�mico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="60%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
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
				<!-- 6.2 - Vice-coordenador de curso de gradua��o ou P�s-Gradua��o-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioViceCoordenadorCurso}">
							<br> <span class="header"><b>6.02 - Vice-coordenador de curso de gradua��o ou P�s-Gradua��o</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="60%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
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
				<!-- 6.3 - Participa��o em Colegiados (excluir os membros natos) - s� contar� o colegiado para o qual o conselheiro foi indicado por elei��o (n�o vale aquele por delega��o do cargo ou representa��o)-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoColegiado}">
							<br> <span class="header"><b>6.03 - Participa��o em Colegiados (excluir os membros natos) - s� contar� o colegiado para o qual o conselheiro foi indicado por elei��o</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
    									 <td WIDTH="10%">Reuni�es</td>
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
				<!-- 6.4 - Participa��o em comiss�o de cria��o de novos cursos e reformula��o de projeto pedag�gico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoNovosCursos}">
							<br> <span class="header"><b>6.04 - Participa��o em comiss�o de cria��o de novos cursos e reformula��o de projeto pedag�gico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
    									 <td WIDTH="10%">Reuni�es</td>
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
				<!-- 6.5 - Participa��o em comiss�o permanente de progress�o horizontal e vertical, designado por portaria-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoPermanente}">
							<br> <span class="header"><b>6.05 - Participa��o em comiss�o permanente de progress�o horizontal e vertical, designado por portaria</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
    									 <td WIDTH="10%">Reuni�es</td>
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
				<!-- 6.6 - Participa��o em comiss�es tempor�rias-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoTemporarias}">
							<br> <span class="header"><b>6.06 - Participa��o em comiss�es tempor�rias</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
    									 <td WIDTH="10%">Reuni�es</td>
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
				<!-- 6.7 - Participa��o em comiss�es de sindic�ncia ou de processos de natureza disciplinar-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioParticipacaoComissaoSindicancia}">
							<br> <span class="header"><b>6.07 - Participa��o em comiss�es de sindic�ncia ou de processos de natureza disciplinar</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 <td WIDTH="50%">Atividade</td>
    									 <td WIDTH="15%">Per�odo</td>
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
				<!-- 6.8 - Chefia ou coordena��o de setores acad�micos de apoio: laborat�rios, n�cleos de estudos, bibliotecas, oficinas ou �rg�o similar, descrito pela PRH como tipo 3 e designado por portaria-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioChefiaCoordenacaoSetoresAcademicoApoio}">
							<br> <span class="header"><b>6.08 - Chefia ou coordena��o de setores acad�micos de apoio: laborat�rios, n�cleos de estudos, bibliotecas, oficinas ou �rg�o similar, descrito pela PRH como tipo 3 e designado por portaria</b></span>
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
				<!-- 6.9 - Coordena��o de Base de Pesquisa-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioChefiaCoordenacaoBasePesquisa}">
							<br> <span class="header"><b>6.09 - Coordena��o de Base de Pesquisa</b></span>
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
				<!-- 6.10 - Coordena��o de curso de p�s-gradua��o lato sensu (devidamente comprovado que n�o recebe remunera��o para esta fun��o)-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioChefiaCoordenacaoCursoLato}">
							<br> <span class="header"><b>6.10 - Coordena��o de curso de p�s-gradua��o lato sensu (devidamente comprovado que n�o recebe remunera��o para esta fun��o)</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="55%">Atividade</td>
									    <td WIDTH="15%">Per�odo</td>
									    <td WIDTH="10%">Remunerado</td>
									    <td WIDTH="10%">Total Pontos</td>
									    <td WIDTH="10%">Pontos GED</td>
								</tr>
							<c:forEach items="${relatorioProdocente.obj.relatorioChefiaCoordenacaoCursoLato}"	var="item">
								<tr>
									<td>${item.chefia.nome}</td>
									<td>${item.periodoInicio} - ${item.periodoFim}</td>
									<td align="center">${item.pago ? "Sim" : "N�o"}</td>
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
									    <td WIDTH="15%">Per�odo</td>
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
									    <td WIDTH="15%">Per�odo</td>
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
									    <td WIDTH="15%">Per�odo</td>
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
									    <td WIDTH="15%">Per�odo</td>
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
									    <td WIDTH="15%">Per�odo</td>
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
				<!-- 6.16 - Consultor "ad hoc" de �rg�o de fomento-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocOrgaoFormento}">
							<br> <span class="header"><b>6.16 - Consultor "ad hoc" de �rg�o de fomento</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Per�odo</td>
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
				<!-- 6.17 - Representa��o acad�mica e participa��o em �rg�os de formula��o e execu��o de pol�ticas p�blicas de ensino, ci�ncia e tecnologia-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioRepresentacaoAcademica}">
							<br> <span class="header"><b>6.17 - Representa��o acad�mica e participa��o em �rg�os de formula��o e execu��o de pol�ticas p�blicas de ensino, ci�ncia e tecnologia</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Per�odo</td>
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
				<!-- 6.18 - Consultor "ad hoc" de comiss�o nacional de reforma e avalia��o de cursos-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioConsultorAdHocReformaAvaliacaoCurso}">
							<br> <span class="header"><b>6.18 - Consultor "ad hoc" de comiss�o nacional de reforma e avalia��o de cursos</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	<td WIDTH="60%">Atividade</td>
									    <td WIDTH="15%">Per�odo</td>
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
		<!-- 6.Atividades Administrativas e de Representa��o -->
		<td></td>
	</tr>
	<tr>
		<td>
		<h3>7.Outras Atividades</h3>
		<table>
			<tr>
				<!-- 7.1 - Participa��o em banca examinadora de concurso p�blico para professor Titular ou Livre Doc�ncia-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorTitular}">
							<br> <span class="header"><b>7.01 - Participa��o em banca examinadora de concurso p�blico para professor Titular ou Livre Doc�ncia</b></span>
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
				<!-- 7.2 - Participa��o em banca examinadora de concurso p�blico para professor Adjunto, Assistente ou Auxiliar-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorAdjAssAux}">
							<br> <span class="header"><b>7.02 - Participa��o em banca examinadora de concurso p�blico para professor Adjunto, Assistente ou Auxiliar</b></span>
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
				<!-- 7.3 - Participa��o em banca examinadora de concurso p�blico para professor Substituto-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorSubstituto}">
							<br> <span class="header"><b>7.03 - Participa��o em banca examinadora de concurso p�blico para professor Substituto</b></span>
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
				<!-- 7.4 - Participa��o em banca examinadora de concurso p�blico para professor N�vel M�dio-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoProfessorMedio}">
							<br> <span class="header"><b>7.04 - Participa��o em banca examinadora de concurso p�blico para professor N�vel M�dio</b></span>
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
				<!-- 7.5 - Participa��o em banca examinadora de concurso p�blico para T�cnico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaConcursoTecnico}">
							<br> <span class="header"><b>7.05 - Participa��o em banca examinadora de concurso p�blico para T�cnico</b></span>
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
				<!-- 7.6 - Participa��o em banca examinadora de Tese de Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaTeseDoutorado}">
							<br> <span class="header"><b>7.06 - Participa��o em banca examinadora de Tese de Doutorado</b></span>
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
				<!-- 7.7 - Participa��o em banca examinadora de Tese ou Disserta��o de Mestrado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaTeseMestrado}">
							<br> <span class="header"><b>7.07 - Participa��o em banca examinadora de Tese ou Disserta��o de Mestrado</b></span>
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
				<!-- 7.8 - Participa��o em banca examinadora de Qualifica��o de Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaQualificacaoDoutorado}">
							<br> <span class="header"><b>7.08 - Participa��o em banca examinadora de Qualifica��o de Doutorado</b></span>
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
				<!-- 7.9 - Participa��o em banca examinadora de Qualifica��o de Mestrado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaQualificacaoMestrado}">
							<br> <span class="header"><b>7.09 - Participa��o em banca examinadora de Qualifica��o de Mestrado</b></span>
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
				<!-- 7.10 - Participa��o em banca examinadora de Monografia de Especializa��o-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaMonografiaEspecializacao}">
							<br> <span class="header"><b>7.10 - Participa��o em banca examinadora de Monografia de Especializa��o</b></span>
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
				<!-- 7.11 - Participa��o em banca examinadora de Sele��o de Doutorado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaSelecaoDoutorado}">
							<br> <span class="header"><b>7.11 - Participa��o em banca examinadora de Sele��o de Doutorado</b></span>
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
				<!-- 7.12 - Participa��o em banca examinadora de Sele��o de Mestrado-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaSelecaoMestrado}">
							<br> <span class="header"><b>7.12 - Participa��o em banca examinadora de Sele��o de Mestrado</b></span>
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
				<!-- 7.13 - Participa��o em Banca Examinadora de Monografia de Gradua��o-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaMonografiaGraduacao}">
							<br> <span class="header"><b>7.13 - Participa��o em Banca Examinadora de Monografia de Gradua��o</b></span>
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
				<!-- 7.14 - Participa��o em banca examinadora de Sele��o de Especializa��o-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioBancaSelecaoEspecializacao}">
							<br> <span class="header"><b>7.14 - Participa��o em banca examinadora de Sele��o de Especializa��o</b></span>
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
				<!-- 7.15 - Disciplina cursada (com aprova��o) como parte de programa de p�s-gradua��o Lato Sensu sem afastamento-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioDisciplinaCursadaLato}">
							<br> <span class="header"><b>7.15 - Disciplina cursada (com aprova��o) como parte de programa de p�s-gradua��o Lato Sensu sem afastamento</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="65%">N�vel do Curso</td>
										    <td WIDTH="15%">Per�odo</td>
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
				<!-- 7.16 - Orientador Acad�mico-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioOrientadorAcademico}">
							<br> <span class="header"><b>7.16 - Orientador Acad�mico</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="50%">Atividade</td>
										    <td WIDTH="15%">Per�odo</td>
										    <td WIDTH="10%">Reuni�es</td>
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
				<!-- 7.17 - Orienta��o de alunos de gradua��o: Inicia��o cient�fica, PET, Inicia��o Tecnol�gica, Extens�o, Monitoria e Apoio T�cnico em atividades acad�micas-->
				<td>
				<p><c:if test="${not empty relatorioProdocente.obj.relatorioOrientacaoAlunosGraduacao1 or not empty relatorioProdocente.obj.relatorioOrientacaoAlunosGraduacao2}">
							<br> <span class="header"><b>7.17 - Orienta��o de alunos de gradua��o: Inicia��o cient�fica, PET, Inicia��o Tecnol�gica, Extens�o, Monitoria e Apoio T�cnico em atividades acad�micas</b></span>
							<br>
							<TABLE style="width:100%" cellpadding=2 cellspacing=0>
								<tr bgcolor="#CECECE">
		 						 	 	 	<td WIDTH="50%">Atividade</td>
										    <td WIDTH="15%">Per�odo</td>
										    <td WIDTH="10%">Reuni�es</td>
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
