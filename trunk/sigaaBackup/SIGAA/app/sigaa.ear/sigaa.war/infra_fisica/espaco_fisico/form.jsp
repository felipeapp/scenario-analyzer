<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName = "espacoFisicoBean"/>

	<h2><ufrn:subSistema/> > Espa�o F�sico </h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Espa�o F�sico.</strong>
		</p>
		<br/>
		<p>
			O cadastro foi dividido em 2 etapas:
			<ol>
				<li><strong>A primeira etapa � respons�vel por descrever o espa�o f�sico.</strong></li>
				<li>Durante a segunda etapa, detalhe os recursos que o espa�o f�sico possui.</li>
			</ol>
		</p>
	
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption>Dados do Espa�o F�sico</caption>
		<tbody>
			<tr>
				<th class="required" width="25%">C�digo Identificador:</th>
				<td>
					<h:inputText id="codigo" value="#{espacoFisicoBean.obj.codigo}" onkeyup="CAPS(this);" size="50" style="width: 90%"  />
					<ufrn:help width="200">
						C�digo �nico que identifique este espa�o. Exemplo: 4A4 
					</ufrn:help>
				</td>
			</tr>					
			<tr>
				<th valign="top">Descri��o do espa�o:</th>
				<td><h:inputTextarea id="descricao" value="#{espacoFisicoBean.obj.descricao}" style="width: 90%" rows="3" /></td>
			</tr>
			<tr>
				<th class="required">Localiza��o do espa�o:</th>
				<td>
					<h:selectOneMenu id="unidadeResponsavel" value="#{espacoFisicoBean.obj.unidadeResponsavel.id}" style="width: 90%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE  --"/>
						<f:selectItems value="#{gestorUnidadesMBean.unidadesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>						
			
			<tr>
				<th>Capacidade:</th>
				<td>
					<h:inputText id="capacidade" value="#{espacoFisicoBean.obj.capacidade}" size="6" onkeyup="return formatarInteiro(this);"/>
					<ufrn:help width="300">
						N�mero de pessoas que o espa�o comporta
					</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>�rea:</th>
				<td><h:inputText id="area" value="#{espacoFisicoBean.obj.area}" size="6" onkeyup="return formatarInteiro(this);"/> m�</td>
			</tr>						
				<tr>
				<th class="required">Tipo:</th>
				<td>
					<h:selectOneMenu id="tipoEspacoFisico" value="#{espacoFisicoBean.obj.tipo.id}" style="width: 50%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE  --"/>
						<f:selectItems value="#{espacoFisicoBean.allTipoEspacoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>				
			
			<tr>
				<th>Espa�o Reservado Prioritariamente para:</th>
				<td>
					<h:selectOneMenu id="unidadeReservada" value="#{espacoFisicoBean.obj.unidadePreferenciaReserva.id}" style="width: 50%">
					<f:selectItem itemValue="0" itemLabel="-- NENHUMA UNIDADE  --"/>
						<f:selectItems value="#{unidade.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Cancelar" action="#{espacoFisicoBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" immediate="true" />
					<h:commandButton value="Pr�ximo >>" action="#{espacoFisicoBean.gerenciarRecursos}" id="proximo"/>
				</td>
			</tr>
		</tfoot>							
		</table>
	</h:form>

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>