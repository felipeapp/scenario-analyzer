<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
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
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2><ufrn:subSistema /> > Dados complementares para a inscri��o
	no processo de sele��o de fiscais</h2>
		<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<p>Para completar a inscri��o para a Sele��o de Fiscais, escolha
		at� cinco locais de aplica��o preferenciais para trabalhar. <c:if
			test="${inscricaoSelecaoFiscalVestibular.exibeOutrasCidades}">
				Se voc� tem disponibilidade para trabalhar em outras cidades no interior
				 onde ser�o aplicadas provas, marque a op��o no formul�rio.<br>
		</c:if></p>
		<p>Voc� ser� alocado de acordo com a necessidade de fiscais da
		COMPERVE, podendo ser alocado em quaisquer dos locais optados ou n�o.</p>
		Para sua identifica��o � necess�rio enviar uma foto em padr�o 3x4.
		Esta foto ser� analisada pela COMPERVE e dever� seguir as
		recomenda��es abaixo. Caso n�o siga as recomenda��es a <b>sua
		inscri��o pode ser invalidada</b>. <br>
		<br>
		<b>Recomenda��es: </b> <br>
		<div class="fotoExemplo"><c:if
			test="${acompanhamentoVestibular.obj.sexoMasculino}">
			<img src="/sigaa/public/images/vestibular/fotoSexoM.jpg" />
		</c:if> <c:if test="${acompanhamentoVestibular.obj.sexoFeminino}">
			<img src="/sigaa/public/images/vestibular/fotoSexoF.jpg" />
		</c:if></div>
		<ul class="alinhar">
			<li>Ser�o aceitas apenas fotos no padr�o 3x4 (o mesmo utilizado
			em documentos de identifica��o).</li>
			<li><b>Envie uma foto recente.</b></li>
			<li>O arquivo a ser enviado dever� estar no formato JPEG (.jpg).
			</li>
			<li>A imagem deve ter boa qualidade de cores e luz, a fim de
			permitir a identifica��o do candidato.</li>
			<li>Se voc� for fazer sua pr�pria foto 3x4, recomendamos:
			<ul>
				<li>Enquadrar a cabe�a inteira, desde o topo at� os ombros,
				vis�o frontal e com os olhos abertos;</li>
				<li>Tirar a foto com um fundo neutro ou branco. Fa�a-a de
				frente � uma parede, ou utilize um len�ol para o plano de fundo;</li>
				<li>Evitar sombras no rosto ou no fundo. Sugerimos fotografar
				em ambientes claros, durante o dia, ou utilizar um flash;</li>
				<li>Exibir uma express�o natural, sem sorrir ou piscar;</li>
				<li>N�o usar �culos escuros, bon� ou chap�u;</li>
				<li>Chamar um amigo ou parente para ajud�-lo.</li>
			</ul>
			</li>
			<li>Envie uma foto com dimens�es m�nimas de <b>600 x 800
			pixels</b>. Caso a foto seja maior, procurar manter as dimens�es 3 x 4. </b>
			</li>
		</ul>
		<p>Para mais informa��es, leia as normas para a sele��o de Fiscais
		constantes no <a href="${linkPublico.linkComissaoVestibular}" target="_blank">s�tio
		da COMPERVE.</a></p>
		</div>
		<h:form enctype="multipart/form-data" id="formFoto">
		<table class="formulario" width="80%">
			<caption>Enviar a Foto 3x4</caption>
				<c:if test="${!inscricaoSelecaoFiscalVestibular.obj.statusFoto.valida}">
					<tr>
						<th class="obrigatorio" width="30%">Arquivo:</th>
						<td>
								<t:inputFileUpload value="#{inscricaoSelecaoFiscalVestibular.foto}" styleClass="file" id="nomeArquivo" />
								<h:commandButton value="Submeter Foto" action="#{inscricaoSelecaoFiscalVestibular.enviarFoto}" id="trocarFoto"/>
						</td>
					</tr>
				</c:if>
			<c:if test="${not empty inscricaoSelecaoFiscalVestibular.foto || not empty inscricaoSelecaoFiscalVestibular.obj.idFoto}">
				<tr>
					<td colspan="2" class="subFormulario">
						Foto 3x4
					</td>
				</tr>
				<tr>
					<td colspan="2" class="foto" style="text-align: center;">
						<c:if test="${not empty inscricaoSelecaoFiscalVestibular.foto}">
							<rich:paint2D id="painter" width="150" height="200" paint="#{inscricaoSelecaoFiscalVestibular.imagemFoto}" />
						</c:if>
						<c:if test="${empty inscricaoSelecaoFiscalVestibular.foto and not empty inscricaoSelecaoFiscalVestibular.obj.idFoto}">
							<img src="${ctx}/verFoto?idArquivo=${inscricaoSelecaoFiscalVestibular.obj.idFoto}&key=${ sf:generateArquivoKey(inscricaoSelecaoFiscalVestibular.obj.idFoto) }" style="width: 150px; height: 200px"/>
						</c:if>
					</td>
				</tr>
			</c:if>
		</table>		
		</h:form> 
		<h:form id="form">
		<table class="subFormulario" width="80%">
			<caption class="listagem">Inscri��o para a Sele��o de Fiscal</caption>
			<tbody>
			<c:if test="${!inscricaoSelecaoFiscalVestibular.processoSeletivoSetado}">
				<tr>
					<th class="required">${inscricaoSelecaoFiscalVestibular.novaInscricao ? 'Processo Seletivo:' : '<b>Processo Seletivo:</b>' } </th>
					<td>
						<h:selectOneMenu id="processoSeletivo" onchange="submit();"
							valueChangeListener="#{inscricaoSelecaoFiscalVestibular.selecionaProcessoSeletivo}"
							value="#{inscricaoSelecaoFiscalVestibular.obj.processoSeletivoVestibular.id}"
							rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.processoSeletivoCombo}" />
						</h:selectOneMenu>
						
						<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.processoSeletivoVestibular.nome}" rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao}" />
						
					 </td>
				</tr>
			</c:if>
			<c:if test="${inscricaoSelecaoFiscalVestibular.processoSeletivoSetado}">
				<tr>
					<th class="rotulo"><b>Processo Seletivo:</b></th>
					<td> 
						<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.processoSeletivoVestibular.nome}" />
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2" class="subFormulario">Escolha o local de prefer�ncia a trabalhar (ser� atendido conforme a necessidade da COMPERVE)</td>
			</tr>
			<c:if test="${inscricaoSelecaoFiscalVestibular.novaInscricao}">
				<c:if test="${!inscricaoSelecaoFiscalVestibular.selecionarMunicipio}">
					<tr>
						<th class="rotulo">Munic�pio:</th>
						<td><h:outputText value="#{inscricaoSelecaoFiscalVestibular.municipio.nome}" /></td>
					</tr>
				</c:if>
	 			<c:if test="${inscricaoSelecaoFiscalVestibular.selecionarMunicipio}">
					<tr>
						<th class="required">Seu Munic�pio:</th>
						<td>
							<h:selectOneMenu onchange="submit();" 
								value="#{inscricaoSelecaoFiscalVestibular.municipio.id}" id="selectMunicipio"
								valueChangeListener="#{inscricaoSelecaoFiscalVestibular.selecionaMunicipio}" 
								rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}" >
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.municipiosCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
			</c:if>
			<tr>
				<th class="required">${inscricaoSelecaoFiscalVestibular.novaInscricao ? 'Local 1:' : '<b>Local 1:</b>' } </th>
				<td>
				<h:selectOneMenu value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[0].id}"
					id="local1" disabled="#{localAplicacaoProva.readOnly}"
					immediate="true" rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM LOCAL --" />
					<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.localAplicacaoProvaCombo}" />
				</h:selectOneMenu>
				
				<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[0].nome}" rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao}" />
				</td>
			</tr>
			<tr>
				<th class="required">${inscricaoSelecaoFiscalVestibular.novaInscricao ? 'Local 2:' : '<b>Local 2:</b>' } </th>
				<td><h:selectOneMenu
					value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[1].id}"
					id="local2" disabled="#{localAplicacaoProva.readOnly}"
					immediate="true" rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM LOCAL --" />
					<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.localAplicacaoProvaCombo}" />
				</h:selectOneMenu>
				
				<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[1].nome}" rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao}" />
				</td>
			</tr>
			<tr>
				<th class="required">${inscricaoSelecaoFiscalVestibular.novaInscricao ? 'Local 3:' : '<b>Local 3:</b>' } </th>
				<td><h:selectOneMenu
					value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[2].id}"
					id="local3" disabled="#{localAplicacaoProva.readOnly}"
					immediate="true" rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM LOCAL --" />
					<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.localAplicacaoProvaCombo}" />
				</h:selectOneMenu>
					
				<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[2].nome}" rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao}" />
				</td>
			</tr>
			<tr>
				<th class="required">${inscricaoSelecaoFiscalVestibular.novaInscricao ? 'Local 4:' : '<b>Local 4:</b>' } </th>
				<td><h:selectOneMenu
					value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[3].id}"
					id="local4" disabled="#{localAplicacaoProva.readOnly}"
					immediate="true" rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM LOCAL --" />
					<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.localAplicacaoProvaCombo}" />
				</h:selectOneMenu>
				
				<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[3].nome}" rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao}" />
				</td>
			</tr>
			<tr>
				<th class="required">${inscricaoSelecaoFiscalVestibular.novaInscricao ? 'Local 5:' : '<b>Local 5:</b>' } </th>
				<td><h:selectOneMenu
					value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[4].id}"
					id="local5" disabled="#{localAplicacaoProva.readOnly}"
					immediate="true" rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM LOCAL --" />
					<f:selectItems value="#{inscricaoSelecaoFiscalVestibular.localAplicacaoProvaCombo}" />
				</h:selectOneMenu>
				
				<h:outputText value="#{inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas[4].nome}" rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao}" />
				</td>
			</tr>
			<c:if test="${inscricaoSelecaoFiscalVestibular.exibeOutrasCidades}">
				<tr>
					<td colspan="2" class="subFormulario">
						Disponibilidade para viajar para outra cidade
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{inscricaoSelecaoFiscalVestibular.obj.disponibilidadeOutrasCidades}" rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}" />
					</th>
					
					<td>
					<c:if test="${!inscricaoSelecaoFiscalVestibular.novaInscricao}">
						<h:outputText  value="(Sim) " rendered="#{inscricaoSelecaoFiscalVestibular.obj.disponibilidadeOutrasCidades}" />
						<h:outputText  value="(N�o) " rendered="#{!inscricaoSelecaoFiscalVestibular.obj.disponibilidadeOutrasCidades}" />
					</c:if>
					<h:outputText value="Tenho disponibilidade para fiscalizar em outras cidades, de acordo com a necessidade da COMPERVE."/> 
					</td>
				</tr>
			</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Participar da Sele��o" action="#{inscricaoSelecaoFiscalVestibular.inscrever}" 
						rendered="#{inscricaoSelecaoFiscalVestibular.novaInscricao}"/><h:commandButton value="Atualizar" action="#{inscricaoSelecaoFiscalVestibular.atualizar}"
						rendered="#{!inscricaoSelecaoFiscalVestibular.novaInscricao && !inscricaoSelecaoFiscalVestibular.obj.statusFoto.valida}"/><h:commandButton value="Cancelar" 
						action="#{inscricaoSelecaoFiscalVestibular.cancelar}" onclick="#{confirm}" immediate="true"
						rendered="#{!inscricaoSelecaoFiscalVestibular.obj.statusFoto.valida}"  />
						<h:commandButton value="<< Voltar" action="#{inscricaoSelecaoFiscalVestibular.cancelar}" 
						rendered="#{inscricaoSelecaoFiscalVestibular.obj.statusFoto.valida}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>