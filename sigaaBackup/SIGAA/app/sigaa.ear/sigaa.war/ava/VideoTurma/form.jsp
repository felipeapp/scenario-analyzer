	<a4j:keepAlive beanName="videoTurma" />
			
			<h:outputText value="#{ videoTurma.associarAVideo}"/>
						
			<div class="descricaoOperacao">
				<p>Prezado(a) docente,</p>
				<p>Preencha este formul�rio para adicionar um v�deo � turma virtual. O(a) senhor(a) pode indicar o endere�o do v�deo na internet ou envi�-lo de seu computador.</p>
				<p>Para os v�deos enviados pelo computador � disponibilizado um relat�rio de acesso dos discentes.</p>
				<p>Para facilitar a visualiza��o dos v�deos pelos discentes, o sistema efetua uma convers�o para um formato mais leve, diminuindo, assim, a quantidade de dados que ser�o transmitidos. 
					<b>Nestes casos, o v�deo no formato original n�o permanece salvo no sistema. Por favor, mantenha uma c�pia do arquivo caso deseje reutiliz�-lo.</b></p>
			</div>
			
			<table class="formAva">
				<tr>
					<th class="required" width="22%">T�tulo:</th>
					<td><h:inputText value="#{ videoTurma.video.titulo }" style="width:400px;" /></td>
				</tr>
				<tr>
					<th style="vertical-align:top;">Descri��o:</th>
					<td><h:inputTextarea styleClass="mceEditor" id="descricao" value="#{ videoTurma.video.descricao }" cols="10" rows="10" style="width:90%;"/></td>
				</tr>
				<tr>
					<th class="required">T�pico de Aula:</th>
					<td>
					<h:selectOneMenu id="aula" value="#{ videoTurma.video.topicoAula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
							<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE UM T�PICO DE AULA -- "/>
							<f:selectItems value="#{topicoAula.comboIdentado}" />
						</h:selectOneMenu>
						<h:selectOneMenu id="semAula" value="#{ videoTurma.video.topicoAula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
							<f:selectItem itemLabel="Nenhum T�pico de Aula foi cadastrado" itemValue="0"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Localiza��o do V�deo:</th>
					<td>
						<h:selectOneRadio value="#{ videoTurma.enviar }" onchange="exibirCampos(this.value);" styleClass="separarLabels" >
							<f:selectItems value="#{ videoTurma.comboEnviar }"/>
							<a4j:support event="onclick" actionListener="#{videoTurma.entrarPortaArquivos}" onsubmit="true" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th class="<h:outputText value="required" rendered="#{ videoTurma.video.id == 0 }" />"><span id="labelArquivo">Arquivo</span><span id="labelLink">Link</span>:</th>
					<td>
						<t:inputFileUpload value="#{ videoTurma.arquivoEnviado }" id="arquivo" />
						<h:inputText value="#{ videoTurma.video.link }" style="width:400px;" id="link" />
						<span id="arquivoPA">
							<h:outputText value="#{ videoTurma.nomeArquivo }" id="nomeArquivo"/>
							<span class="descricao-campo">(Arquivo a ser enviado para a Turma Virtual.)</span> 
						</span>	
					</td>
				</tr>
				<tr id="formaExibicao">
					<th>Forma de Exibi��o:</th>
					<td>
						<h:selectOneRadio value="#{ videoTurma.video.telaCheia }" styleClass="separarLabels" onclick="exibirResolucoes(this.value);">
							<f:selectItem itemValue="true" itemLabel="Nova Janela" />
							<f:selectItem itemValue="false" itemLabel="Player Interno" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th><span id="labelResolucao">Resolu��o (em pixels):</span></th>
					<td>
						<span id="valorResolucao">
							<h:selectOneRadio value="#{ videoTurma.video.altura }" styleClass="separarLabels">
								<f:selectItems value="#{ videoTurma.resolucoesCombo }" />
							</h:selectOneRadio>
						</span>
					</td>
				</tr>
				<tr>
					<th>Notifica��o:</th>
					<td>
						<h:selectBooleanCheckbox id="notificacao" value="#{ videoTurma.notificarAlunos }"/>
						<ufrn:help>Notificar os alunos por e-mail</ufrn:help>
					</td>
				</tr>
				<c:if test="${exibirTurmas}">
					<tr>
						<th class="required">Criar em: </th>
						<td>
							<t:selectManyCheckbox id="criarEm" value="#{ videoTurma.cadastrarEm }" layout="pageDirection">
								<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasPermissaoNoticia }"/>
							</t:selectManyCheckbox>
						</td>				
					</tr>
				</c:if>
			</table>
			
	<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
	<script>
		function exibirCampos (enviar){

			if (enviar == ""){
				var nomeArquivo = document.getElementById("formAva:nomeArquivo");
				if (nomeArquivo.innerHTML != null && nomeArquivo.innerHTML != "")
					enviar = "P";
			}
			
			var arquivo = document.getElementById("formAva:arquivo");
			var link = document.getElementById("formAva:link");
			var arquivoPA = document.getElementById("arquivoPA");
			
			var labelArquivo = document.getElementById("labelArquivo");
			var labelLink = document.getElementById("labelLink");

			var formaExibicao = document.getElementById("formaExibicao");
			
			arquivo.style.display = "none";
			link.style.display = "none";
			arquivoPA.style.display = "none";
			labelArquivo.style.display = "none";
			labelLink.style.display = "none";
			formaExibicao.style.display = "none";
			
			if (enviar == "I"){
				arquivo.style.display = "inline";
				labelArquivo.style.display = "inline";
				formaExibicao.style.display = "";				
			} else if (enviar == "E"){
				link.style.display = "inline";
				labelLink.style.display = "inline";
				formaExibicao.style.display = "none";	
			} else if (enviar == "P"){
				arquivoPA.style.display = "inline";
				labelArquivo.style.display = "inline";
				formaExibicao.style.display = "";	
			} else {
				arquivo.style.display = "inline";
				labelArquivo.style.display = "inline";
				formaExibicao.style.display = "";	
			}
		}

		function exibirResolucoes (popUp){

			var label = document.getElementById("labelResolucao");
			var valor = document.getElementById("valorResolucao");

			label.style.display = "none";
			valor.style.display = "none";
			
			if (popUp == "false"){
				label.style.display = "inline";
				valor.style.display = "inline";
			}
		}

		jQuery(document).ready(function () {
			exibirCampos("${videoTurma.enviar}");
			exibirResolucoes("${videoTurma.video.telaCheia}");
		});

		<c:if test="${turmaVirtual.acessoMobile == false}">
			tinyMCE.init({
				mode : "textareas", editor_selector : "mceEditor", theme : "advanced", width : "460", height : "150", language : "pt",
				theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
				theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
				theme_advanced_buttons3 : "", plugins : "searchreplace,contextmenu,advimage", theme_advanced_toolbar_location : "top", theme_advanced_toolbar_align : "left",
				setup : function(ed) {
					ed.onChange.add(function(ed) {
					tinyMCE.triggerSave();
					});
				}
			});
		</c:if>
	</script>
	