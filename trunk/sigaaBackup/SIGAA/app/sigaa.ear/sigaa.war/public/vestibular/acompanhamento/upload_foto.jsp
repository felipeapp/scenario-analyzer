<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<style>
	div.descricaoOperacao {
		margin: 10px 15px 15px;
	}

	div.fotoExemplo {
		float: right;
		margin: 10px 0px 5px 10px;
		*margin: 5px 0px 5px 10px;
	}
	
	ul.alinhar li {
		text-align: justify;
	}
</style>

<f:view>
	<h2>Vestibular > Enviar Foto</h2>

	<h:panelGrid rendered="#{acompanhamentoVestibular.permiteAlterarFoto}">
		<div class="descricaoOperacao">
			
			Para sua identifica��o � necess�rio enviar uma foto em padr�o 3x4. Esta foto ser� analisada pela COMPERVE e dever� seguir as recomenda��es abaixo. Caso n�o siga as recomenda��es
			a <b>sua inscri��o pode ser invalidada</b>. 
		
			<br><br>
			<b>Recomenda��es: </b> 
			<br><br>
			<div class="fotoExemplo">
				<c:if test="${acompanhamentoVestibular.obj.sexoMasculino}">
					<img src="../../images/vestibular/fotoSexoM.jpg" />
				</c:if>
				<c:if test="${acompanhamentoVestibular.obj.sexoFeminino}">
					<img src="../../images/vestibular/fotoSexoF.jpg" />
				</c:if>
			</div>
			<ul class="alinhar">
				<li> Ser�o aceitas apenas fotos no padr�o 3x4 (o mesmo utilizado em documentos de identifica��o).</li>
				<li> <b>Envie uma foto recente.</b> </li>
				<li> O arquivo a ser enviado dever� estar no formato JPEG (.jpg). </li>
				<li> A imagem deve ter boa qualidade de cores e luz, a fim de permitir a identifica��o do candidato. </li>
				<li> Se voc� for fazer sua pr�pria foto 3x4, recomendamos:
					<ul>
						<li>Enquadrar a cabe�a inteira, desde o topo at� os ombros, vis�o frontal e com os olhos abertos;</li>
						<li>Tirar a foto com um fundo neutro ou branco. Fa�a-a de frente � uma parede, ou utilize um len�ol para o plano de fundo;</li>
						<li>Evitar sombras no rosto ou no fundo. Sugerimos fotografar em ambientes claros, durante o dia, ou utilizar um flash;</li> 
						<li>Exibir uma express�o natural, sem sorrir ou piscar;</li>
						<li>N�o usar �culos escuros, bon� ou chap�u;</li>
						<li>Chamar um amigo ou parente para ajud�-lo.</li>
					</ul>
				</li>
				<li> Envie uma foto com dimens�es m�nimas de <b>600 x 800 pixels</b>. Caso a foto seja maior, procurar manter as dimens�es 3 x 4. </b> </li>
			</ul>
			
		</div>
	</h:panelGrid>
	<h:panelGrid rendered="#{not acompanhamentoVestibular.permiteAlterarFoto}">
		<div class="descricaoOperacao">
			
			<p>Caro Candidato,</p>
			<br/> 
			<c:if test="${acompanhamentoVestibular.obj.statusFoto.valida}">
				<p>Sua foto j� foi <b>validada</b> pela Comiss�o do Vestibular e por
				este motivo <b>n�o poder� ser alterada</b>.</p>
			</c:if>
			<c:if test="${!acompanhamentoVestibular.obj.statusFoto.valida}">
				<p>Sua foto j� foi <b>invalidada</b> pela Comiss�o do Vestibular pelo seguinte motivo:<br/><br/>
					<center><b>${acompanhamentoVestibular.obj.statusFoto.descricao}</b></center>
				</p><br/>
				<p>A Comiss�o do Vestibular define um per�odo para que o candidato envie a foto (de 
				<ufrn:format type="data" valor="${acompanhamentoVestibular.processoSeletivo.inicioInscricaoCandidato}"/>
				a 
				<ufrn:format type="data" valor="${acompanhamentoVestibular.processoSeletivo.fimInscricaoCandidato}"/>
				ou de 
				<ufrn:format type="data" valor="${acompanhamentoVestibular.processoSeletivo.inicioLimiteAlteracaoFotos}"/>
				a 
				<ufrn:format type="data" valor="${acompanhamentoVestibular.processoSeletivo.fimLimiteAlteracaoFotos}"/>) 
				e por este motivo <b>n�o poder� ser alterada</b>.</p>
			</c:if>
			
		</div>
	</h:panelGrid>
	<br/>
	<h:form enctype="multipart/form-data" id="form">
		<table class="formulario" style="width: 55%;">
			<caption>Enviar a Foto 3x4</caption>
			<tbody>
				<c:if test="${acompanhamentoVestibular.permiteAlterarFoto}">
					<tr>
						<th class="obrigatorio" width="30%" >Arquivo:</th>
						<td>
							<t:inputFileUpload value="#{acompanhamentoVestibular.arquivoUpload}" styleClass="file" id="nomeArquivo" onchange="$('fakeInput').value = this.value;"/>
						</td>
					</tr>
					<tr>
						<td colspan="10" align="center">
							<h:commandButton value="Trocar Foto" action="#{acompanhamentoVestibular.enviarArquivo}" id="trocarFoto" style="width: 30%"
								rendered="#{acompanhamentoVestibular.foto != null || acompanhamentoVestibular.obj.idFoto != null}" /> 
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty acompanhamentoVestibular.foto || not empty acompanhamentoVestibular.obj.idFoto}">
					<tr>
						<td colspan="2" class="subFormulario">
							Foto
						</td>
					</tr>
					<tr>
						<td colspan="2" class="foto" style="text-align: center;">
							<c:if test="${not empty acompanhamentoVestibular.arquivoUpload}">
								<rich:paint2D id="painter" width="150" height="200" paint="#{acompanhamentoVestibular.imagemFoto}" />
							</c:if>
							<c:if test="${empty acompanhamentoVestibular.arquivoUpload and not empty acompanhamentoVestibular.obj.idFoto}">
								<img src="${ctx}/verFoto?idArquivo=${acompanhamentoVestibular.obj.idFoto}&key=${ sf:generateArquivoKey(acompanhamentoVestibular.obj.idFoto) }" style="width: 150px; height: 200px"/>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="subFormulario">
							Dados do Arquivo
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<c:if test="${not empty acompanhamentoVestibular.dadosFoto}">
								<c:forEach items="#{acompanhamentoVestibular.dadosFoto}" var="dado" varStatus="status">
									<c:if test="${status.index > 0}">, </c:if>
									<b>${dado.key}</b>: ${dado.value}
								</c:forEach>
							</c:if>
						</td>
					</tr>
				</c:if>
				<tr>
					<td colspan="2" style="text-align: center;">
						<h:commandButton value="Enviar Foto" action="#{acompanhamentoVestibular.enviarArquivo}"  rendered="#{acompanhamentoVestibular.foto == null && acompanhamentoVestibular.obj.idFoto == null}" id="enviarArquivo"/>
					</td>
				</tr>
			</tbody>
		</table>
		</h:form>
		<h:form id="formBotoes">
			<table class="formulario" style="width: 55%;">
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="<< Passo Anterior" action="#{acompanhamentoVestibular.formDadosPessoais}" id="passoAnterior"/>&ensp;
							<h:commandButton value="Cancelar" action="#{acompanhamentoVestibular.cancelar}" onclick="#{confirm}" id="cancelar"/>&ensp;
							<h:commandButton value="Pr�ximo Passo >>" action="#{acompanhamentoVestibular.submeterFoto}" rendered="#{acompanhamentoVestibular.foto != null || acompanhamentoVestibular.obj.idFoto != null}" id="submeterFoto"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/public/include/rodape.jsp"%>