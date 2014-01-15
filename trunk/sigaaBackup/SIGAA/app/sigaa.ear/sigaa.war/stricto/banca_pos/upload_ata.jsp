<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Anexar Ata > Enviar Arquivo</h2>

	<h:form id="form" enctype="multipart/form-data">
		<c:set value="#{ataBancaMBean.discente}" var="discente"></c:set>
		<%@include file="/graduacao/info_discente.jsp"%>
		<table class="formulario" width="85%">
			<caption class="formulario">Dados da Banca</caption>
			<tr>
				<th width="20%">Tipo: </th>
				<td> ${ataBancaMBean.bancaSelecionada.tipoDescricao }
				</td>
			</tr>
			<tr>
				<th>Local: </th>
				<td> ${ataBancaMBean.bancaSelecionada.local }
				</td>
			</tr>
			<tr>
				<th>Data:</th>
				<td> <ufrn:format type="data" valor="${ataBancaMBean.bancaSelecionada.data}" ></ufrn:format> </td>
			</tr>
			<%--
			<tr>
				<th>Trabalho Final: </th>
				<td>${ataBancaMBean.obj.trabalhoFinal.descricao }
				</td>
			</tr>
			--%>
			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Dados do Trabalho</caption>
						<tr>
							<th width="20%">Título:</th>
							<td>${ataBancaMBean.bancaSelecionada.dadosDefesa.titulo}
							</td>
						</tr>
						<tr>
							<th>Páginas:</th>
							<td>${ataBancaMBean.bancaSelecionada.dadosDefesa.paginas}
							</td>
						</tr>
						<tr>
							<th>Grande Área:</th>
							<td>${ataBancaMBean.bancaSelecionada.grandeArea.nome} </td>
						</tr>
						<tr>
							<th>Área:</th>
							<td>${ataBancaMBean.bancaSelecionada.area.nome} </td>
						</tr>
						<tr>
							<th>Sub-Área:</th>
							<td>${ataBancaMBean.bancaSelecionada.subArea.nome} </td>
						</tr>
						<tr>
							<th>Especialidade:</th>
							<td>${ataBancaMBean.bancaSelecionada.especialidade.nome} </td>
						</tr>
						<tr>
							<th valign="top">Resumo:</th>
							<td>${ataBancaMBean.bancaSelecionada.dadosDefesa.resumo}
							</td>
						</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Membros da Banca</caption>
					<tbody>
					<c:forEach items="${ataBancaMBean.bancaSelecionada.membrosBanca}" var="membro" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td width="15%" >${membro.tipoDescricao}</td>
							<td align="left">${membro.membroIdentificacao }</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</td>
			</tr>

<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Ata de parecer da banca</caption>
						<tr>
							<td> Caminho do arquivo: </td>
							<td> <t:inputFileUpload id="uFile" value="#{ataBancaMBean.arquivo}" storage="file" size="50" /> </td>						
						</tr>
						
						<tr>							
							<c:set var="arqAta" value="#{ataBancaMBean.bancaSelecionada.idArquivo}"/>
							<td>
								<c:if test="${not empty arqAta}">
									Ata atual: <html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${arqAta}"> <img src="/sigaa/img/pdf.png" title="Abrir Ata" /> </html:link>
								</c:if>
							</td>	
						</tr>
						
				</table>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton id="confir" value="Enviar Ata "
						action="#{ataBancaMBean.selecionarArquivo}" />
					<h:commandButton value="Cancelar" id="cancelamento" onclick="#{confirm}"
						action="#{ataBancaMBean.cancelar}" />					
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
