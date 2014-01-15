<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN} %>">
	<li>Conv�nios de Est�gio
		<ul>
			<li> <h:commandLink action="#{convenioEstagioMBean.iniciar}" value="Cadastrar Conv�nio de Est�gio" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{convenioEstagioMBean.iniciarConsulta}" value="Listar/Alterar Conv�nios de Est�gio" onclick="setAba('geral')" /> </li>
			<li> 
				<h:commandLink action="#{convenioEstagioMBean.iniciarAnalise}" value="Analisar Conv�nios de Est�gio Submetidos" onclick="setAba('geral')" /> 
				<h:outputText value=" (#{ moduloConvenioEstagioMBean.qtdConveniosPendentesAnalise })" rendered="#{ moduloConvenioEstagioMBean.qtdConveniosPendentesAnalise > 0}"
					style="color: red;"/> 
			</li>
		</ul>
	</li>
	<li>Oferta de Est�gio
		<ul>
			<li> <h:commandLink action="#{ofertaEstagioMBean.iniciar}" value="Cadastrar Oferta de Est�gio" onclick="setAba('geral')" /> </li>			
			<li> <h:commandLink action="#{ofertaEstagioMBean.iniciarConsulta}" value="Listar/Alterar Oferta de Est�gio" onclick="setAba('geral')" /> </li>				
			<li> <h:commandLink action="#{buscaEstagioMBean.iniciar}" value="Gerenciar Estagi�rios" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{estagioMBean.iniciarCadastroAvulso}" value="Cadastrar Estagi�rios Avulso" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	<li>Relat�rios de Est�gio
		<ul>
			<li> <h:commandLink action="#{questionarioBean.iniciarCadastroEstagio}" value="Cadastrar" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{questionarioBean.gerenciarQuestionarioEstagio}" value="Listar/Alterar" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	</ufrn:checkRole>
</ul>