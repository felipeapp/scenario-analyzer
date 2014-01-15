<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>

	<h2 class="tituloPagina"> <ufrn:subSistema/> > Consulta de Defesas</a></h2>
	
	<h:form id="listagem">
	<table class="visualizacao" width=80%>
		<caption> Dados da Defesa </caption>
		
		<tbody>
		
			<tr>
				<th width="15%"> Discente: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.dadosDefesa.discente.matriculaNome}" />
				</td>
			</tr>
			<tr>
				<th width="15%"> CPF: </th>
				<td>
					 <ufrn:format type="cpf_Cnpj" valor="${consultarDefesaMBean.obj.dadosDefesa.discente.pessoa.cpf_cnpj}"/>
				</td>
			</tr>						
			<tr>
				<th width="15%"> Email: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.dadosDefesa.discente.pessoa.email}" />
				</td>
			</tr>			
			<tr>
				<th width="15%"> Orientador: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.dadosDefesa.discente.orientacao.nomeOrientador}" 
						rendered="#{not empty consultarDefesaMBean.obj.dadosDefesa.discente.orientacao}" id="outputTextOrientador"/>
					<h:outputText value="Não informado" rendered="#{empty consultarDefesaMBean.obj.dadosDefesa.discente.orientacao}" id="outputTextNaoInformado"/>
				</td>
			</tr>
			<tr>
				<th width="15%"> Co-Orientador: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.dadosDefesa.discente.coOrientacao.nomeOrientador}"
						rendered="#{not empty consultarDefesaMBean.obj.dadosDefesa.discente.coOrientacao}" id="outputTextCoOrientador"/>
					<h:outputText value="Não informado" rendered="#{empty consultarDefesaMBean.obj.dadosDefesa.discente.coOrientacao}" id="textCoOrientadorNaoInformado"/>
				</td>
			</tr>
			
			<tr>
				<th> Local: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.local}" id="outputTextLocal"/>
					<h:outputText value="Não informado" rendered="#{empty consultarDefesaMBean.obj.local}" id="textLocalNaoInformado"/>
				</td>
			</tr>
			
			<tr>
				<th> Data: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.data}" id="outputTextDataDefesa"/>
					<h:outputText value="Não informado" rendered="#{empty consultarDefesaMBean.obj.data}" id="textDataDefesaNaoInformada"/>
				</td>
			</tr>
			
			<tr>
				<th>Hora: </th>
				<td>
					<ufrn:format type="hora" valor="${consultarDefesaMBean.obj.data}" ></ufrn:format>
					<h:outputText value="Não informado" rendered="#{empty consultarDefesaMBean.obj.data}" id="textHorarioDefesaNaoInformada"/>
				</td>
			</tr>
			
			<tr>
				<th> Tipo da banca: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.tipoDescricao}" id="textDescricaoTipoBanca"></h:outputText>
				</td>
			</tr>
			<tr>
				<th> Número de páginas: </th>
				<td>
					<h:outputText value="#{consultarDefesaMBean.obj.dadosDefesa.paginas}" id="outputTextNumPaginas"/>
					<h:outputText value="Não informado" rendered="#{empty consultarDefesaMBean.obj.dadosDefesa.paginas}" id="textNumPaginasNaoInformado"/>
				</td>
			</tr>

			<tr>
				<th> Link para o Arquivo (BDTD ${ configSistema['siglaInstituicao'] }): </th>
				<td>
					<c:if test="${not empty consultarDefesaMBean.obj.dadosDefesa.linkArquivo}">
						<a href="${consultarDefesaMBean.obj.dadosDefesa.linkArquivo}" target="_blank"><h:graphicImage value="/img/pdf.png" title="Visualizar" /></a>
					</c:if>
					<c:if test="${empty consultarDefesaMBean.obj.dadosDefesa.linkArquivo}">
						Nenhum endereço informado.
					</c:if>
					
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario"> Ata de parecer da banca </td>
			</tr>
			<tr>							
				<td colspan="2">
					<c:set var="arqAta" value="#{consultarDefesaMBean.obj.idArquivo}"/>
					<c:if test="${not empty arqAta}">
						Ata atual: <html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${arqAta}"> <img src="/sigaa/img/pdf.png" title="Abrir Ata" /> </html:link>
					</c:if>
				</td>	
			</tr>				
			<tr>
				<td colspan="2" class="subFormulario"> Título</td>
			</tr>
			<tr>
				<td colspan="2" style="font-weight: bold; font-size: large;">
					${consultarDefesaMBean.obj.dadosDefesa.tituloStripHtml}
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario"> Resumo </td>
			</tr>
			<tr>
				<td colspan="2">
					${consultarDefesaMBean.obj.dadosDefesa.resumo}
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario"> Palavas-Chave </td>
			</tr>
			<tr>
				<td colspan="2">
					${consultarDefesaMBean.obj.dadosDefesa.palavrasChave}
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario"> Membros da Banca </td>
			</tr>
			<tr>
				<td colspan="2">
					<table style="width: 100%">
						<thead>
							<tr>
								<th style="text-align: left">CPF</th>
								<th style="text-align: left">Nome</th>
								<th style="text-align: left">Email</th>
								<th style="text-align: left">Instituição</th>
								<th style="text-align: left">Tipo</th>
							</tr>
						</thead>
						<c:forEach var="membros" items="#{consultarDefesaMBean.obj.membrosBanca}">
							<tr>
								<td>
									<ufrn:format type="cpf_cnpj" valor="${membros.pessoa.cpf_cnpj}"/>
								</td>
								<td>
									${membros.pessoa.nome}
								</td>
								<td>
									${membros.pessoa.email}
								</td>																								
								<td> 
									<c:if test="${membros.externo}">
										${membros.instituicao.sigla}	
									</c:if>
									<c:if test="${not membros.externo}">
										${ configSistema['siglaInstituicao'] }
									</c:if>
								</td>
								<td>
									${membros.tipoDescricao}
								</td>									
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<c:if test="${empty consultarDefesaMBean.obj.membrosBanca}">
				<tr>
					<td colspan="2">Não há membros cadastrados para a banca </td>
				</tr>
			</c:if>
			<c:if test="${consultarDefesaMBean.obj.descricaoStatus eq 'CANCELADA'}">
			<tr>
				<td colspan="2" class="subFormulario"> Justificativa de Cancelamento </td>
			</tr>
			<tr>
				<td colspan="2">
					${consultarDefesaMBean.obj.observacao}
				</td>
			</tr>
			</c:if>					
		</tbody>
	</table>
	<br>
	<div align="center">
		<%-- Não usar, dá problema do IE <a href="javascript:history.go(-1)"> << Voltar</a> --%>
		<h:commandLink value="<< Voltar" action="#{consultarDefesaMBean.telaConsulta}" />
	</div>

	</h:form>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>