<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	span.info {
		font-size: 0.95em;
		color: #555;
		line-height: 1.3em;
	}
	.btnExcluirFoto{
		background-color: #EFEFEF;
		padding: 2px 13px;
		font-weight: normal;
		font-variant: normal;
	}
	.fotoPerfil{
		background-color: #EFEFEF;
		border:#EFEFEF 8px solid;
	}
	
	.confirmaSenha { width: 60% !important; }
</style>
<f:view>

	<h2> Editar Perfil </h2>

	<div class="descricaoOperacao">
		<p>Caro discente,</p><br/>
		<p>
			A foto adicionada ao seu perfil será utilizada no Portal da Turma como parte
			de sua identificação para os outros alunos e professores.
		</p>
		<p>
			Sugerimos discrição ao selecioná-la.
		</p>
	</div>

	<h:form  enctype="multipart/form-data">
		<table class="formulario" style="width: 75%">
		<caption> Dados do perfil </caption>
		<tr >
			<td style="vertical-align:top;" rowspan="2" align="center">
				<c:if test="${usuario.idFoto != null}">
					<img id="fotoPerfil" class="fotoPerfil" src="/sigaa/verFoto?idFoto=${usuario.idFoto}&key=${ sf:generateArquivoKey(usuario.idFoto) }" width="100" height="125" />
					<br/>
					<h:commandLink id="btnExcluirFoto" styleClass="btnExcluirFoto" value="X Excluir Foto"
					 action="#{perfilDiscente.cadastrar}" onclick="#{confirmDelete}"  >
						<f:param id="idFoto" name="idFoto" value="#{usuario.idFoto}" />
					</h:commandLink>
				</c:if>
				<c:if test="${usuario.idFoto == null}">
					<img id="fotoPerfil" class="fotoPerfil" src="${ctx}/img/no_picture.png" width="100" height="125" />
				</c:if>
				<br />
			</td>	
		</tr>
		<tr>
			<td style="vertical-align:top;"> <b>Alterar foto:</b> <br/>
				<input type="radio" name="tipoenviofoto" id="tipoenviofotoarquivo" onchange="exibirFlash();" checked="checked" onclick="exibirFlash();" /> <label for="tipoenviofotoarquivo">Enviando um arquivo do seu computador:</label><br/>
				<div id="fotoArquivo">
					<t:inputFileUpload id="alterarFoto" value="#{perfilDiscente.foto}" size="50"/><br/><br/>
				</div>
				
				<input type="radio" name="tipoenviofoto" id="tipoenviofotowebcam" onchange="exibirFlash();" onclick="exibirFlash();" /> <label for="tipoenviofotowebcam">Tirando uma foto com sua webcam:</label><br/>
				<div id="fotoFlash">
					<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab" width="210" height="155" id="webcam">
						<param name="movie" value="/sigaa/geral/swf/CameraFotoSigaa.swf" />
						<param name="wmode" value="transparent" />
						<embed src="/sigaa/geral/swf/CameraFotoSigaa.swf" wmode="transparent" />
					</object>
				</div>
				
				<script>
				
					function atualizarImagem (imagem) {
						document.getElementById("fotoPerfil").src = imagem;
					}
				
					function exibirFlash (){
						if (document.getElementById("tipoenviofotoarquivo").checked){
							document.getElementById("fotoArquivo").style.display = "block";
							document.getElementById("fotoFlash").style.display = "none";
						} else {
							document.getElementById("fotoArquivo").style.display = "none";
							document.getElementById("fotoFlash").style.display = "block";
						}
					}
					
					exibirFlash();
				</script>
			</td>
		</tr>
		<tr>
			<td colspan="2" alt="Descrição Pessoal">&nbsp;<b> Descrição Pessoal:</b>	<br/>
			&nbsp;&nbsp;<h:inputTextarea id="descricaoPessoal" title="Descrição Pessoal" value="#{perfilDiscente.obj.descricao}" rows="5" style="width: 95%"/> </td>
		</tr>
		<tr>
			<td colspan="2" alt="Áreas de Interesse" title="Áreas de Interesse">&nbsp;<b> Áreas de Interesse:</b>
			<span class="info">(Áreas de interesse de ensino e pesquisa) </span> 
			<br/>
			 &nbsp;&nbsp;<t:inputTextarea id="areasInteresse" value="#{perfilDiscente.obj.areas}" style="width: 95%" title="Áreas de Interesse"/> </td>
		</tr>
		<tr>
			<td  colspan="2" alt="Currículo Lattes">&nbsp;<b> Currículo Lattes: </b><br/>
			&nbsp;&nbsp;<t:inputText id="curriculoLattes" value="#{perfilDiscente.obj.enderecoLattes}"style="width: 96%" alt="Currículo Lattes" title="Currículo Lattes" maxlength="300"/> </td>
		</tr>
		<tr>
			<td colspan="2" alt="Assinatura (utilizada nas mensagens da caixa postal)" title="Assinatura (utilizada nas mensagens da caixa postal)">&nbsp;&nbsp;<b>Assinatura</b> <span class="info">(Utilizada nas mensagens da caixa postal)</span> <br/>
				&nbsp;&nbsp;<h:inputTextarea value="#{perfilDiscente.obj.assinatura}" title="Assinatura (utilizada nas mensagens da caixa postal)" style="width: 95%;" rows="3" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div align="center">
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</div>
			</td>	
		</tr>		
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{perfilDiscente.cadastrar}" value="Gravar Perfil"/>
				<h:commandButton action="#{perfilDiscente.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}" />
			</td>
		</tr>
		</tfoot>
 		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
