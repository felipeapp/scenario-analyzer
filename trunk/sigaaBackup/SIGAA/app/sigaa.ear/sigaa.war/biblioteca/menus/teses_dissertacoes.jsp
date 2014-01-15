<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_GESTOR_BDTD, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
<ul>
	<li>Publica��o
		<ul>
			<li>
				<h:commandLink action="#{consultarDefesaMBean.iniciar}" onclick="setAba('teses_dissertacoes')" value="Catalogar Teses/Disserta��es"> 
					<f:param name="catalogar" value="true"/>
				</h:commandLink> 
				<ufrn:help> 
					<p> Cataloga as Teses e Disserta��es no acervo das bibliotecas.</p>
					<br/>
					<p> Caso a cataloga��o da Tese/Disserta��o j� exista, � poss�vel alterar as suas informa��o por essa op��o. </p>
					<br/>
					<p> <strong>Observa��o:</strong> S� � poss�vel verificar se a cataloga��o existe caso ela seja catalogada por essa opera��o, ou pela opera��o existente no setor de processos  t�cnicos: <i> Processos T�cnicos -> Cataloga��o -> Catalogar Defesa </i> </p>
				</ufrn:help>
			</li>
			<%-- S� pode ir para produ��o depois da tarefa #43902 Fluxo de Publica��o de Tese/Disserta��o
			<li>
				<h:commandLink action="#{termoPublicacaoTD.iniciarPublicacao}" onclick="setAba('teses_dissertacoes')" value="Teses/Disserta��es Pendentes de Publica��o na BDTD " />
				<blink style="color:red;font-weight: bold;">( ${termoPublicacaoTD.pendentePublicacao} )</blink>
			</li>
			--%>			
		</ul>
	</li>
	
	<li>Consultas
		<ul>
			<%-- S� pode ir para produ��o depois da tarefa #43902 Fluxo de Publica��o de Tese/Disserta��o
			<li><h:commandLink action="#{termoPublicacaoTD.buscarDiscente}" onclick="setAba('teses_dissertacoes')" value="Buscar/Listar Solicita��es (TEDE)" /></li>
			--%>
			<li><h:commandLink action="#{consultarDefesaMBean.iniciar}" onclick="setAba('teses_dissertacoes')" value="Consultar Bancas" /></li>						
		</ul>
	</li>
</ul>
</ufrn:checkRole>