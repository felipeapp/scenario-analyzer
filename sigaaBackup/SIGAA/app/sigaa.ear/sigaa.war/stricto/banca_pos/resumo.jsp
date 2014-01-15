<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema/> > Banca de Pós > Resumo</h2>

	<h:form id="form">
		<c:set value="#{bancaPos.discente}" var="discente"></c:set>
		<%@include file="/graduacao/info_discente.jsp"%>
		<table class="visualizacao" width="85%">
			<caption class="formulario">Dados da Banca</caption>
			<tr>
				<th width="20%">Tipo: </th>
				<td> ${bancaPos.obj.tipoDescricao }
				</td>
			</tr>
			<tr>
				<th>Local: </th>
				<td> ${bancaPos.obj.local }
				</td>
			</tr>
			<tr>
				<th>Data:</th>
				<td> <ufrn:format type="data" valor="${bancaPos.obj.data}" ></ufrn:format> </td>
			</tr>
			<tr>
				<th>Hora: </th>
				<td>
					<ufrn:format type="hora" valor="${bancaPos.obj.data}" ></ufrn:format>
				</td>
			</tr>
			<c:if test="${bancaPos.obj.matriculaComponente != null}" >
				<tr>
					<th>Atividade:</th>
					<td>
						 ${bancaPos.obj.matriculaComponente.componenteDescricao} (${bancaPos.obj.matriculaComponente.anoPeriodo}) 
						 - ${bancaPos.obj.matriculaComponente.situacaoMatricula.descricao}
					</td>
				</tr>
			</c:if>
			<tr>
				<th> Link para o Arquivo (BDTD ${ configSistema['siglaInstituicao'] }): </th>
				<td>
					<c:if test="${not empty bancaPos.obj.dadosDefesa.linkArquivo}">
						<a href="${bancaPos.obj.dadosDefesa.linkArquivo}" target="_blank"><h:graphicImage value="/img/pdf.png" title="Visualizar" /></a>
					</c:if>
					<c:if test="${empty bancaPos.obj.dadosDefesa.linkArquivo}">
						Nenhum endereço informado.
					</c:if>
					
				</td>
			</tr>
			<%--
			<tr>
				<th>Trabalho Final: </th>
				<td>${bancaPos.obj.trabalhoFinal.descricao }
				</td>
			</tr>
			--%>
			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Dados do Trabalho</caption>
						<tr>
							<th width="20%" valign="top">Título:</th>
							<td>${bancaPos.obj.dadosDefesa.titulo}
							</td>
						</tr>
						<tr>
							<th width="20%" valign="top">Palavras chave:</th>
							<td>${bancaPos.obj.dadosDefesa.palavrasChave}
							</td>
						</tr>
						<tr>
							<th>Páginas:</th>
							<td>${bancaPos.obj.dadosDefesa.paginas}
							</td>
						</tr>
						<tr>
							<th>Grande Área:</th>
							<td>${bancaPos.obj.dadosDefesa.area.grandeArea.nome} </td>
						</tr>
						<tr>
							<th>Área:</th>
							<td>${bancaPos.obj.dadosDefesa.area.area.nome} </td>
						</tr>
						<c:if test="${bancaPos.obj.dadosDefesa.area.subarea.id > 0}">
							<tr>
								<th>Sub-Área:</th>
								<td>${bancaPos.obj.dadosDefesa.area.subarea.nome} </td>
							</tr>
							<c:if test="${bancaPos.obj.dadosDefesa.area.especialidade.id > 0}">
								<tr>
									<th>Especialidade:</th>
									<td>${bancaPos.obj.dadosDefesa.area.especialidade.nome} </td>
								</tr>
							</c:if>
						</c:if>
						<tr>
							<th valign="top">Resumo:</th>
							<td>${bancaPos.obj.dadosDefesa.resumo}
							</td>
						</tr>
				</table>
				</td>
			</tr>

			<c:if test="${not bancaPos.defesaAntiga}">
			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Membros da Banca</caption>
					<tbody>
					<c:forEach items="${bancaPos.obj.membrosBanca}" var="membro" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td width="15%" >${membro.tipoDescricao}</td>
							<td align="left">${membro.membroIdentificacao }</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</td>
			</tr>
			</c:if>

			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center">
						<c:if test="${not bancaPos.defesaAntiga}">
							<h:commandButton id="confir" value="Confirmar " action="#{bancaPos.confirmar}" />
							<h:commandButton id="dados" value="<< Dados Gerais" action="#{bancaPos.telaDadosBanca}" />
							<h:commandButton id="membros" value="<< Membros da Banca" action="#{bancaPos.telaMembros}" />
							<h:commandButton value="Cancelar" id="cancelamento" onclick="#{confirm}" action="#{bancaPos.cancelar}" />
						</c:if>

						<c:if test="${bancaPos.defesaAntiga}">
							<h:commandButton id="confir" value="Confirmar " action="#{bancaPos.confirmar}" />
							<h:commandButton id="dados" value="<< Dados Gerais" action="#{bancaPos.telaDadosBanca}" />
							<h:commandButton value="Cancelar" id="cancelamento" onclick="#{confirm}" action="#{bancaPos.cancelar}" />
						</c:if>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
