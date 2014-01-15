<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
    <a4j:keepAlive beanName="termoPublicacaoTD"/>
	<h:form id="form" enctype="multipart/form-data">

		<h2><ufrn:subSistema /> > Termo de Autorização para Publicação
		de Testes e Dissertações - TEDE</h2>

		<div class="descricaoOperacao">Complete ou edite as informações
		abaixo e clique em "Emitir TEDE" para obter o Termo de Autorização de
		Publicação de Teses e Dissertações preenchidos.</div>

		<table class="formulario">
			<caption>Informe os dados para a emissão do TEDE</caption>
			<tr>
				<td colspan="6" class="subFormulario">Identificação do Autor</td>
			</tr>
			
			<tr>
				<td colspan="6">
					<table width="100%" class="visualizacao">
						<tr>
							<th width="15%">Autor:</th>
							<td colspan="5"><h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.nome}" /></td>
						</tr>
						<tr>
							<th>Identidade:</th>
							<td colspan="5"><h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.identidade.numero }" /> - <h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.identidade.orgaoExpedicao }" />
							/ <h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.identidade.unidadeFederativa.sigla }" />
							</td>
						</tr>
						<tr>
							<th>CPF:</th>
							<td colspan="5"><ufrn:format type="cpf_cnpj"
								valor="${termoPublicacaoTD.obj.discente.pessoa.cpf_cnpj}" /></td>
						</tr>
						<tr>
							<th>E-mail:</th>
							<td colspan="5"><h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.email}" /></td>
						</tr>
						<tr>
							<th>Telefone:</th>
							<td colspan="5"><h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.telefone}" /></td>
						</tr>					
					</table>
				</td>			
			</tr>
			
			<tr>
				<td colspan="6" class="subFormulario">Dados da <h:outputText
					value="#{termoPublicacaoTD.tipoDocumento}" /></td>
			</tr>			
			<tr>
				<td colspan="6">
					<table width="100%" class="visualizacao">
						<tr>			
							<th width="15%">Título:</th>
							<td colspan="5">${termoPublicacaoTD.obj.banca.dadosDefesa.titulo}</td>
						</tr>
						<tr>
							<th>Palavras-chave:</th>
							<td colspan="5">${termoPublicacaoTD.obj.banca.dadosDefesa.palavrasChave}</td>
						</tr>
						<tr>
							<th>Data da Defesa:</th>
							<td colspan="2"><h:outputText
								value="#{termoPublicacaoTD.obj.banca.data}" /></td>
							<th>Titulação:</th>
							<td colspan="2"><h:outputText
								value="#{termoPublicacaoTD.titulacao}" /></td>
						</tr>
						<tr>
							<th>Instituição de Defesa:</th>
							<td colspan="2">${ configSistema['nomeInstituicao']}/${ configSistema['siglaInstituicao']}</td>
							<th>CNPJ:</th>
							<td colspan="2">${ configSistema['cnpjInstituicao']}</td>
						</tr>					
					</table>
				</td>
			</tr>						

			<tr>
				<th width="20%">Afiliação:</th>
				<td colspan="2">
					<h:inputText value="#{termoPublicacaoTD.obj.afiliacao}" size="60" maxlength="100" id="afiliacaoo"/>
					<ufrn:help img="/img/ajuda.gif">Instituição de vínculo empregatício do autor.</ufrn:help>
				</td>
				<th>CNPJ:</th>
				<td colspan="2">
					<h:inputText value="#{termoPublicacaoTD.obj.CNPJAfiliacao}" size="18" id="cpfAfiliacao" maxlength="18" 
					onkeypress="formataCNPJ(this, event, null)" onblur="formataCNPJ(this, event, null)" converter="convertCpf"/>
					<ufrn:help img="/img/ajuda.gif">CNPJ da Instituição de vínculo empregatício do autor.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Agência de Fomento:</th>
				<td colspan="5">
					<h:selectOneMenu id="instituicaoFormento" value="#{termoPublicacaoTD.obj.instituicaoFomento.id}">
						<f:selectItem itemValue="0" itemLabel="-- Selecione a Instituição de Formento --"/>
						<f:selectItems value="#{instituicaoFomento.allCombo}"/>
					</h:selectOneMenu>
			</tr>
			<tr>
				<td colspan="6" class="subFormulario">Orientador / Membros da
				banca</td>
			</tr>
			<tr>
				<th style="font-weight: bold;">Orientador:</th>
				<td>
					<h:outputText value="#{termoPublicacaoTD.obj.discente.orientacao.nomeOrientador}" />
					<h:outputText value="NÃO INFORMADO" rendered="#{empty termoPublicacaoTD.obj.discente.orientacao.nomeOrientador}" style="color: red;"/>
				</td>
				<th style="font-weight: bold;">CPF:</th>
				<td>
					<ufrn:format type="cpf_cnpj" valor="${termoPublicacaoTD.obj.discente.orientacao.pessoa.cpf_cnpj}" />
				</td>
				<th style="font-weight: bold;">E-mail:</th>
				<td>
					<h:outputText value="#{termoPublicacaoTD.obj.discente.orientacao.pessoa.email}" />
				</td>
			</tr>
			<c:if
				test="${not empty termoPublicacaoTD.obj.banca.membrosBanca}">
				<c:forEach items="#{termoPublicacaoTD.obj.banca.membrosBanca}"
					var="membro">
					<tr>
						<th style="font-weight: bold;">Membro da banca:</th>
						<td><h:outputText value="#{membro.nome}" /></td>
						<th style="font-weight: bold;">CPF:</th>
						<td><ufrn:format type="cpf_cnpj"
							valor="${membro.pessoa.cpf_cnpj}" /></td>
						<th style="font-weight: bold;">E-mail:</th>
						<td><h:outputText value="#{membro.pessoa.email}" /></td>
					</tr>
				</c:forEach>
			</c:if>
			<tr>
				<td colspan="6" class="subFormulario">Outras Informações</td>
			</tr>
			<tr>
				<th>Liberação para publicação:</th>
				<td colspan="2"><h:selectOneRadio
					value="#{termoPublicacaoTD.obj.parcial}" id="tipoPublicacao" onclick="informarRestricoes(this);">
					<f:selectItem id="item1" itemLabel="Total" itemValue="false" />
					<f:selectItem id="item2" itemLabel="Parcial" itemValue="true" />
				</h:selectOneRadio></td>
				<th class="obrigatorio">A Partir do Dia:</th>
				<td colspan="3">
					<t:inputCalendar
						value="#{termoPublicacaoTD.obj.dataPublicacao}" size="10"
						maxlength="10" renderAsPopup="true" popupDateFormat="dd/MM/yyyy"
						renderPopupButtonAsImage="true"
						onkeypress="return formataData(this, event)" id="dataAPartirDoDia">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr id="restricoes">
				<th class="obrigatorio">Restrições:</th>
				<td colspan="5">
					<h:inputText value="#{termoPublicacaoTD.obj.restricoes}" size="65" maxlength="180" id="resstricoes"/>
					<ufrn:help img="/img/ajuda.gif">Especifique o(s) arquivo(s) restrito(s), Capítulo(s), etc, ao qual a publicação se limita. </ufrn:help>
				</td>
			</tr>
			
			<c:if test="${(termoPublicacaoTD.portalCoordenadorStricto || termoPublicacaoTD.portalPpg) && 
				termoPublicacaoTD.obj.banca.dadosDefesa.idArquivo != null}">
				
				<tr>
					<th>Arquivo anexado pelo aluno:</th>
					<td colspan="5">
						<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${termoPublicacaoTD.obj.banca.dadosDefesa.idArquivo}">							
							<img src="/shared/img/icones/download.png" title="Download do arquivo"/>
							Download do arquivo
						</html:link>
					</td>
				</tr>								
			</c:if>			
			<tr>
				<th>
					<h:outputText rendered="#{termoPublicacaoTD.portalDiscente}">Anexar Arquivo:</h:outputText>
					<h:outputText rendered="#{termoPublicacaoTD.portalCoordenadorStricto || 
					termoPublicacaoTD.portalPpg}">Substituir arquivo anexado:</h:outputText>
						
					<c:if test="${termoPublicacaoTD.portalDiscente}">
						<span class="obrigatorio">&nbsp;</span>
					</c:if>
				
				</th>
				<td colspan="5">
					<t:inputFileUpload value="#{ termoPublicacaoTD.arquivo }" id="anexo" size="80"/>
					<ufrn:help>Selecione o arquivo a ser enviado. Apenas no formato (.pdf)</ufrn:help>
				</td>
			</tr>	
			<tr>
				<td colspan="6">
					<c:if test="${termoPublicacaoTD.portalCoordenadorStricto || termoPublicacaoTD.portalPpg}">
						<c:set var="exibirApenasSenha" value="true"></c:set>
					</c:if>
			        <%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>				
				</td>
			</tr>		
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton action="#{termoPublicacaoTD.cadastrar}" value="Emitir TEDE" id="emitirTede"/> 
						<h:commandButton action="#{termoPublicacaoTD.cancelar}" onclick="#{confirm}" value="Cancelar" id="btcancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>				
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


<script type="text/javascript">
function informarRestricoes(obj){
	if (obj.checked == true && obj.value == 'true') {
		$('restricoes').show();
	} else {
		$('restricoes').hide();
	}	
}
  informarRestricoes(document.getElementById('form:tipoPublicacao:1'));
</script>